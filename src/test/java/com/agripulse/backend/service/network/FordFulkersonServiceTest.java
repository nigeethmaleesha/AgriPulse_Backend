package com.agripulse.backend.service.network;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FordFulkersonServiceTest {

    private final FordFulkersonService service = new FordFulkersonService();

    @Test
    void classicalGraphShouldReturnMaximumFlow23() {
        FlowNetwork network = new FlowNetwork(
                List.of("S", "V1", "V2", "V3", "V4", "T"),
                List.of(
                        new FlowEdge("S", "V1", 16),
                        new FlowEdge("S", "V2", 13),
                        new FlowEdge("V1", "V2", 10),
                        new FlowEdge("V2", "V1", 4),
                        new FlowEdge("V1", "V3", 12),
                        new FlowEdge("V3", "V2", 9),
                        new FlowEdge("V2", "V4", 14),
                        new FlowEdge("V4", "V3", 7),
                        new FlowEdge("V3", "T", 20),
                        new FlowEdge("V4", "T", 4)
                )
        );

        MaxFlowComputation result = service.computeMaxFlow(network, "S", "T");

        assertEquals(23, result.maximumFlowKgPerDay());
        assertFalse(result.augmentingPaths().isEmpty());
    }

    @Test
    void demoLayeredNetworkShouldReturnMaximumFlow1350() {
        FlowNetwork network = new FlowNetwork(
                List.of("SOURCE", "F1", "F2", "F3", "H1", "H2", "FACTORY"),
                List.of(
                        new FlowEdge("SOURCE", "F1", 700),
                        new FlowEdge("SOURCE", "F2", 600),
                        new FlowEdge("SOURCE", "F3", 500),
                        new FlowEdge("F1", "H1", 500),
                        new FlowEdge("F1", "H2", 200),
                        new FlowEdge("F2", "H1", 200),
                        new FlowEdge("F2", "H2", 400),
                        new FlowEdge("F3", "H2", 400),
                        new FlowEdge("H1", "FACTORY", 650),
                        new FlowEdge("H2", "FACTORY", 700)
                )
        );

        MaxFlowComputation result = service.computeMaxFlow(network, "SOURCE", "FACTORY");
        assertEquals(1350, result.maximumFlowKgPerDay());
    }

    @Test
    void unreachableSinkShouldReturnZero() {
        FlowNetwork network = new FlowNetwork(
                List.of("S", "A", "T"),
                List.of(new FlowEdge("S", "A", 10))
        );
        assertEquals(0, service.computeMaxFlow(network, "S", "T").maximumFlowKgPerDay());
    }

    @Test
    void duplicateNodeShouldFail() {
        FlowNetwork network = new FlowNetwork(
                List.of("S", "s", "T"),
                List.of(new FlowEdge("S", "T", 10))
        );
        assertThrows(IllegalArgumentException.class,
                () -> service.computeMaxFlow(network, "S", "T"));
    }
}
