package com.agripulse.backend.controller;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.service.network.NetworkAnalysisService;
import com.agripulse.backend.service.network.SupplyNetworkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

    private final SupplyNetworkService supplyNetworkService;
    private final NetworkAnalysisService networkAnalysisService;

    public NetworkController(SupplyNetworkService supplyNetworkService,
                             NetworkAnalysisService networkAnalysisService) {
        this.supplyNetworkService = supplyNetworkService;
        this.networkAnalysisService = networkAnalysisService;
    }

    @GetMapping("/graph")
    public GraphResponse graph() {
        return supplyNetworkService.getGraph();
    }

    @GetMapping("/nodes")
    public List<NodeResponse> nodes() {
        return supplyNetworkService.getNodes();
    }

    @PostMapping("/nodes")
    @ResponseStatus(HttpStatus.CREATED)
    public NodeResponse createNode(@Valid @RequestBody CreateNodeRequest request) {
        return supplyNetworkService.createNode(request);
    }

    @PutMapping("/nodes/{id}")
    public NodeResponse updateNode(@PathVariable Long id, @Valid @RequestBody UpdateNodeRequest request) {
        return supplyNetworkService.updateNode(id, request);
    }

    @DeleteMapping("/nodes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNode(@PathVariable Long id) {
        supplyNetworkService.deleteNode(id);
    }

    @GetMapping("/edges")
    public List<EdgeResponse> edges() {
        return supplyNetworkService.getEdges();
    }

    @PostMapping("/edges")
    @ResponseStatus(HttpStatus.CREATED)
    public EdgeResponse createEdge(@Valid @RequestBody CreateEdgeRequest request) {
        return supplyNetworkService.createEdge(request);
    }

    @PutMapping("/edges/{id}")
    public EdgeResponse updateEdge(@PathVariable Long id, @Valid @RequestBody UpdateEdgeRequest request) {
        return supplyNetworkService.updateEdge(id, request);
    }

    @DeleteMapping("/edges/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEdge(@PathVariable Long id) {
        supplyNetworkService.deleteEdge(id);
    }

    @PostMapping("/max-flow")
    public MaxFlowResponse maxFlow(@Valid @RequestBody RunMaxFlowRequest request) {
        return networkAnalysisService.runMaxFlow(request);
    }

    @GetMapping("/max-flow/results")
    public List<AlgorithmTestResultResponse> recentResults() {
        return networkAnalysisService.getRecentResults();
    }
}
