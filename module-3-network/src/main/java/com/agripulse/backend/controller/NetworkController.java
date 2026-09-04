package com.agripulse.backend.controller;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.service.network.BottleneckService;
import com.agripulse.backend.service.network.FlowNetwork;
import com.agripulse.backend.service.network.NetworkAnalysisService;
import com.agripulse.backend.service.network.SupplyNetworkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Complete Module 3 network API.
 *
 * Task 3A / Member 5 owns graph management and Ford-Fulkerson max flow.
 * Task 3B / Member 6 reuses that graph and max-flow engine for bottleneck
 * detection, ranking, and in-memory capacity scenarios.
 */
@RestController
@RequestMapping("/api/network")
public class NetworkController {

    private final SupplyNetworkService supplyNetworkService;
    private final NetworkAnalysisService networkAnalysisService;
    private final BottleneckService bottleneckService;

    public NetworkController(
            SupplyNetworkService supplyNetworkService,
            NetworkAnalysisService networkAnalysisService,
            BottleneckService bottleneckService
    ) {
        this.supplyNetworkService = supplyNetworkService;
        this.networkAnalysisService = networkAnalysisService;
        this.bottleneckService = bottleneckService;
    }

    // ---------------------------------------------------------------------
    // Shared graph / Task 3A endpoints
    // ---------------------------------------------------------------------

    @GetMapping("/graph")
    public GraphResponse graph() {
        return supplyNetworkService.getGraph();
    }

    /** Clears the saved Module 3 network so a user can build it manually. */
    @DeleteMapping("/graph")
    public GraphResponse clearGraph() {
        return supplyNetworkService.clearGraph();
    }

    /** Replaces the saved Module 3 network with the reproducible coursework demo graph. */
    @PostMapping("/graph/demo")
    public GraphResponse loadDemoGraph() {
        return supplyNetworkService.replaceWithDemoNetwork();
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
    public NodeResponse updateNode(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNodeRequest request
    ) {
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
    public EdgeResponse updateEdge(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEdgeRequest request
    ) {
        return supplyNetworkService.updateEdge(id, request);
    }

    @DeleteMapping("/edges/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEdge(@PathVariable Long id) {
        supplyNetworkService.deleteEdge(id);
    }

    /** Member 5: calculate maximum tea throughput. */
    @PostMapping("/max-flow")
    public MaxFlowResponse maxFlow(@Valid @RequestBody RunMaxFlowRequest request) {
        return networkAnalysisService.runMaxFlow(request);
    }

    @GetMapping("/max-flow/results")
    public List<AlgorithmTestResultResponse> recentResults() {
        return networkAnalysisService.getRecentResults();
    }

    // ---------------------------------------------------------------------
    // Task 3B / Member 6 endpoints
    // ---------------------------------------------------------------------

    /**
     * Runs linear saturated-link detection, max-heap utilization ranking,
     * and exact closure-impact ranking on the database graph.
     */
    @PostMapping("/bottlenecks/analyze")
    public BottleneckAnalysisResponse analyzeBottlenecks(
            @Valid @RequestBody BottleneckAnalysisRequest request
    ) {
        FlowNetwork network = supplyNetworkService.buildActiveFlowNetwork();
        int topN = request.topN() == null ? 10 : request.topN();

        return bottleneckService.analyze(
                network,
                request.sourceCode(),
                request.sinkCode(),
                topN
        );
    }

    /** Runs one temporary capacity scenario without changing PostgreSQL. */
    @PostMapping("/bottlenecks/scenario")
    public CapacityScenarioResultResponse runCapacityScenario(
            @Valid @RequestBody RunCapacityScenarioRequest request
    ) {
        FlowNetwork network = supplyNetworkService.buildActiveFlowNetwork();

        return bottleneckService.runScenario(
                network,
                request.sourceCode(),
                request.sinkCode(),
                request.scenario()
        );
    }

    /** Runs several scenarios against one shared baseline maximum flow. */
    @PostMapping("/bottlenecks/scenarios")
    public CapacityScenarioBatchResponse runCapacityScenarios(
            @Valid @RequestBody RunCapacityScenariosRequest request
    ) {
        FlowNetwork network = supplyNetworkService.buildActiveFlowNetwork();

        return bottleneckService.runScenarios(
                network,
                request.sourceCode(),
                request.sinkCode(),
                request.scenarios()
        );
    }

    @GetMapping("/bottlenecks/presets")
    public Map<String, Object> bottleneckPresets() {
        return Map.of(
                "scenarioTypes", List.of(
                        "CLOSE_LINK",
                        "REDUCE_BY_PERCENT",
                        "INCREASE_BY_PERCENT",
                        "SET_CAPACITY"
                ),
                "candidateMethods", List.of(
                        "Linear saturated-edge scan",
                        "Max-heap utilization ranking",
                        "Ford-Fulkerson closure scenario reruns"
                )
        );
    }
}
