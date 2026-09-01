package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.model.CapacityEdge;
import com.agripulse.backend.model.NodeType;
import com.agripulse.backend.model.SupplyNode;
import com.agripulse.backend.repository.CapacityEdgeRepository;
import com.agripulse.backend.repository.SupplyNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class SupplyNetworkService {

    private final SupplyNodeRepository nodeRepository;
    private final CapacityEdgeRepository edgeRepository;

    public SupplyNetworkService(SupplyNodeRepository nodeRepository, CapacityEdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Transactional(readOnly = true)
    public List<NodeResponse> getNodes() {
        return nodeRepository.findAllByOrderByIdAsc().stream().map(this::toNodeResponse).toList();
    }

    @Transactional
    public NodeResponse createNode(CreateNodeRequest request) {
        String code = normalizeCode(request.code());
        if (nodeRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Node code already exists: " + code);
        }
        enforceSingleEndpointNode(request.nodeType(), null);
        boolean active = request.active() == null || request.active();
        SupplyNode saved = nodeRepository.save(new SupplyNode(code, request.name().trim(), request.nodeType(), active));
        return toNodeResponse(saved);
    }

    @Transactional
    public NodeResponse updateNode(Long id, UpdateNodeRequest request) {
        SupplyNode node = nodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        enforceSingleEndpointNode(request.nodeType(), node.getId());
        node.setName(request.name().trim());
        node.setNodeType(request.nodeType());
        node.setActive(request.active());
        return toNodeResponse(nodeRepository.save(node));
    }

    @Transactional
    public void deleteNode(Long id) {
        SupplyNode node = nodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + id));
        boolean isUsed = edgeRepository.findAll().stream()
                .anyMatch(edge -> edge.getFromNode().getId().equals(id) || edge.getToNode().getId().equals(id));
        if (isUsed) {
            throw new IllegalArgumentException("Cannot delete node because one or more capacity edges use it");
        }
        nodeRepository.delete(node);
    }

    @Transactional(readOnly = true)
    public List<EdgeResponse> getEdges() {
        return edgeRepository.findAllByOrderByIdAsc().stream().map(this::toEdgeResponse).toList();
    }

    @Transactional
    public EdgeResponse createEdge(CreateEdgeRequest request) {
        SupplyNode from = findNode(request.fromCode());
        SupplyNode to = findNode(request.toCode());
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("fromCode and toCode cannot be the same");
        }
        validateDomainTransition(from, to);
        if (edgeRepository.existsByFromNodeAndToNode(from, to)) {
            throw new IllegalArgumentException("Capacity edge already exists: " + from.getCode() + " -> " + to.getCode());
        }
        boolean active = request.active() == null || request.active();
        CapacityEdge saved = edgeRepository.save(new CapacityEdge(
                from, to, request.capacityKgPerDay(), active, trimToNull(request.label())
        ));
        return toEdgeResponse(saved);
    }

    @Transactional
    public EdgeResponse updateEdge(Long id, UpdateEdgeRequest request) {
        CapacityEdge edge = edgeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Edge not found: " + id));
        edge.setCapacityKgPerDay(request.capacityKgPerDay());
        edge.setActive(request.active());
        edge.setLabel(trimToNull(request.label()));
        return toEdgeResponse(edgeRepository.save(edge));
    }

    @Transactional
    public void deleteEdge(Long id) {
        if (!edgeRepository.existsById(id)) {
            throw new IllegalArgumentException("Edge not found: " + id);
        }
        edgeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public GraphResponse getGraph() {
        return new GraphResponse(getNodes(), getEdges());
    }

    @Transactional(readOnly = true)
    public FlowNetwork buildActiveFlowNetwork() {
        List<SupplyNode> nodes = nodeRepository.findAllByOrderByIdAsc().stream()
                .filter(SupplyNode::isActive)
                .toList();
        List<String> activeCodes = nodes.stream().map(SupplyNode::getCode).toList();
        var activeCodeSet = SetUtils.caseInsensitiveSet(activeCodes);

        List<FlowEdge> edges = edgeRepository.findAllByOrderByIdAsc().stream()
                .filter(CapacityEdge::isActive)
                .filter(edge -> edge.getFromNode().isActive() && edge.getToNode().isActive())
                .filter(edge -> activeCodeSet.contains(edge.getFromNode().getCode())
                        && activeCodeSet.contains(edge.getToNode().getCode()))
                .map(edge -> new FlowEdge(
                        edge.getFromNode().getCode(),
                        edge.getToNode().getCode(),
                        edge.getCapacityKgPerDay()
                ))
                .toList();
        return new FlowNetwork(activeCodes, edges);
    }

    private void enforceSingleEndpointNode(NodeType type, Long currentNodeId) {
        if (type != NodeType.SOURCE && type != NodeType.FACTORY) {
            return;
        }
        nodeRepository.findFirstByNodeType(type).ifPresent(existing -> {
            if (currentNodeId == null || !existing.getId().equals(currentNodeId)) {
                throw new IllegalArgumentException("Only one " + type + " node is allowed in the Module 3 graph");
            }
        });
    }

    private void validateDomainTransition(SupplyNode from, SupplyNode to) {
        boolean valid = switch (from.getNodeType()) {
            case SOURCE -> to.getNodeType() == NodeType.FARM;
            case FARM -> to.getNodeType() == NodeType.HUB;
            case HUB -> to.getNodeType() == NodeType.FACTORY;
            case FACTORY -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Module 3 database graph must follow SOURCE -> FARM -> HUB -> FACTORY. Invalid edge: "
                            + from.getCode() + "(" + from.getNodeType() + ") -> "
                            + to.getCode() + "(" + to.getNodeType() + ")"
            );
        }
    }

    private SupplyNode findNode(String code) {
        return nodeRepository.findByCodeIgnoreCase(normalizeCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Node not found: " + code));
    }

    private NodeResponse toNodeResponse(SupplyNode node) {
        return new NodeResponse(node.getId(), node.getCode(), node.getName(), node.getNodeType(), node.isActive());
    }

    private EdgeResponse toEdgeResponse(CapacityEdge edge) {
        return new EdgeResponse(
                edge.getId(), edge.getFromNode().getCode(), edge.getToNode().getCode(),
                edge.getCapacityKgPerDay(), edge.isActive(), edge.getLabel()
        );
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static final class SetUtils {
        private static java.util.Set<String> caseInsensitiveSet(List<String> values) {
            java.util.Set<String> set = new java.util.HashSet<>();
            for (String value : values) {
                set.add(value.toUpperCase(Locale.ROOT));
            }
            return set;
        }
    }
}
