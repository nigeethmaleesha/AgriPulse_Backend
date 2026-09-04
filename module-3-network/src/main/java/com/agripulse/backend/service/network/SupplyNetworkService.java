package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.model.CapacityEdge;
import com.agripulse.backend.model.NodeType;
import com.agripulse.backend.model.SupplyNode;
import com.agripulse.backend.repository.CapacityEdgeRepository;
import com.agripulse.backend.repository.SupplyNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        validateExistingConnectionsForTypeChange(node, request.nodeType());
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
            throw new IllegalArgumentException("Cannot delete location because one or more transport connections use it. Delete those connections first.");
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
        validateNewEdge(from, to, null);

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

        SupplyNode from = isBlank(request.fromCode()) ? edge.getFromNode() : findNode(request.fromCode());
        SupplyNode to = isBlank(request.toCode()) ? edge.getToNode() : findNode(request.toCode());
        validateNewEdge(from, to, edge.getId());

        edge.setFromNode(from);
        edge.setToNode(to);
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

    /**
     * Clears only Module 3 graph data. Algorithm benchmark history is kept.
     * This makes it possible to build a network manually from an empty graph.
     */
    @Transactional
    public GraphResponse clearGraph() {
        edgeRepository.deleteAll();
        edgeRepository.flush();
        nodeRepository.deleteAll();
        nodeRepository.flush();
        return new GraphResponse(List.of(), List.of());
    }

    /**
     * Replaces the current Module 3 graph with the reproducible coursework demo
     * network. This is deliberately explicit so demo data is not confused with
     * user-entered operational data.
     */
    @Transactional
    public GraphResponse replaceWithDemoNetwork() {
        edgeRepository.deleteAll();
        edgeRepository.flush();
        nodeRepository.deleteAll();
        nodeRepository.flush();
        populateDemoNetwork();
        return getGraph();
    }

    /** Used at startup only when SEED_DEMO=true. */
    @Transactional
    public void seedDemoIfEmpty() {
        if (nodeRepository.count() == 0) {
            populateDemoNetwork();
        }
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

    private void validateNewEdge(SupplyNode from, SupplyNode to, Long currentEdgeId) {
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("From location and to location cannot be the same");
        }
        validateDomainTransition(from.getNodeType(), from.getCode(), to.getNodeType(), to.getCode());

        edgeRepository.findAll().stream()
                .filter(existing -> currentEdgeId == null || !existing.getId().equals(currentEdgeId))
                .filter(existing -> existing.getFromNode().getId().equals(from.getId())
                        && existing.getToNode().getId().equals(to.getId()))
                .findFirst()
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Transport connection already exists: "
                            + from.getCode() + " -> " + to.getCode());
                });
    }

    private void validateExistingConnectionsForTypeChange(SupplyNode node, NodeType proposedType) {
        if (node.getNodeType() == proposedType) {
            return;
        }

        for (CapacityEdge edge : edgeRepository.findAll()) {
            if (edge.getFromNode().getId().equals(node.getId())) {
                validateDomainTransition(proposedType, node.getCode(), edge.getToNode().getNodeType(), edge.getToNode().getCode());
            }
            if (edge.getToNode().getId().equals(node.getId())) {
                validateDomainTransition(edge.getFromNode().getNodeType(), edge.getFromNode().getCode(), proposedType, node.getCode());
            }
        }
    }

    private void enforceSingleEndpointNode(NodeType type, Long currentNodeId) {
        if (type != NodeType.SOURCE && type != NodeType.FACTORY) {
            return;
        }
        nodeRepository.findFirstByNodeType(type).ifPresent(existing -> {
            if (currentNodeId == null || !existing.getId().equals(currentNodeId)) {
                throw new IllegalArgumentException("Only one " + type + " location is allowed in the Module 3 network");
            }
        });
    }

    private void validateDomainTransition(NodeType fromType, String fromCode, NodeType toType, String toCode) {
        boolean valid = switch (fromType) {
            case SOURCE -> toType == NodeType.FARM;
            case FARM -> toType == NodeType.HUB;
            case HUB -> toType == NodeType.FACTORY;
            case FACTORY -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Module 3 network must follow SOURCE -> FARM -> HUB -> FACTORY. Invalid connection: "
                            + fromCode + "(" + fromType + ") -> " + toCode + "(" + toType + ")"
            );
        }
    }

    private void populateDemoNetwork() {
        Map<String, SupplyNode> n = new LinkedHashMap<>();
        n.put("SOURCE", nodeRepository.save(new SupplyNode("SOURCE", "Daily Tea Supply Source", NodeType.SOURCE, true)));
        n.put("F1", nodeRepository.save(new SupplyNode("F1", "Tea Farm 1", NodeType.FARM, true)));
        n.put("F2", nodeRepository.save(new SupplyNode("F2", "Tea Farm 2", NodeType.FARM, true)));
        n.put("F3", nodeRepository.save(new SupplyNode("F3", "Tea Farm 3", NodeType.FARM, true)));
        n.put("H1", nodeRepository.save(new SupplyNode("H1", "Collection Hub 1", NodeType.HUB, true)));
        n.put("H2", nodeRepository.save(new SupplyNode("H2", "Collection Hub 2", NodeType.HUB, true)));
        n.put("FACTORY", nodeRepository.save(new SupplyNode("FACTORY", "Tea Factory", NodeType.FACTORY, true)));

        saveDemoEdge(n, "SOURCE", "F1", 700, "Daily available harvest from Farm 1");
        saveDemoEdge(n, "SOURCE", "F2", 600, "Daily available harvest from Farm 2");
        saveDemoEdge(n, "SOURCE", "F3", 500, "Daily available harvest from Farm 3");
        saveDemoEdge(n, "F1", "H1", 500, "Farm 1 to Hub 1 handling/road capacity");
        saveDemoEdge(n, "F1", "H2", 200, "Farm 1 to Hub 2 handling/road capacity");
        saveDemoEdge(n, "F2", "H1", 200, "Farm 2 to Hub 1 handling/road capacity");
        saveDemoEdge(n, "F2", "H2", 400, "Farm 2 to Hub 2 handling/road capacity");
        saveDemoEdge(n, "F3", "H2", 400, "Farm 3 to Hub 2 handling/road capacity");
        saveDemoEdge(n, "H1", "FACTORY", 650, "Hub 1 to Factory daily transport capacity");
        saveDemoEdge(n, "H2", "FACTORY", 700, "Hub 2 to Factory daily transport capacity");
    }

    private void saveDemoEdge(Map<String, SupplyNode> nodes, String from, String to, long capacity, String label) {
        edgeRepository.save(new CapacityEdge(nodes.get(from), nodes.get(to), capacity, true, label));
    }

    private SupplyNode findNode(String code) {
        return nodeRepository.findByCodeIgnoreCase(normalizeCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Location not found: " + code));
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
        if (isBlank(code)) {
            throw new IllegalArgumentException("Location code is required");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
