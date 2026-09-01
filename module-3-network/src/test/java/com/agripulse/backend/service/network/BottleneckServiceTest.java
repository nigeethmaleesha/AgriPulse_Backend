package com.agripulse.backend.service.network;

import com.agripulse.backend.dto.*;
import com.agripulse.backend.service.network.FlowEdge;
import com.agripulse.backend.service.network.FlowNetwork;
import com.agripulse.backend.service.network.FordFulkersonService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BottleneckServiceTest {

    private final FordFulkersonService fordFulkersonService =
            new FordFulkersonService();
    private final BottleneckService service =
            new BottleneckService(fordFulkersonService);

    private FlowNetwork demoNetwork() {
        return new FlowNetwork(
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
    }

    @Test
    void fullMember6AnalysisShouldUseMember5BaselineFlow() {
        BottleneckAnalysisResponse response = service.analyze(
                demoNetwork(), "SOURCE", "FACTORY", 10
        );

        assertEquals(1350L, response.baselineMaximumFlowKgPerDay());
        assertFalse(response.linearSaturatedLinks().isEmpty());
        assertFalse(response.heapRankedLinks().isEmpty());
        assertEquals(10, response.exactClosureImpactRanking().size());
        assertEquals(3, response.methodPerformance().size());
    }

    @Test
    void closingH1ToFactoryShouldReduceThroughputBy650() {
        CapacityScenarioRequest scenario = new CapacityScenarioRequest(
                "Close H1 to Factory",
                CapacityScenarioType.CLOSE_LINK,
                "H1",
                "FACTORY",
                null,
                null
        );

        CapacityScenarioResultResponse result = service.runScenario(
                demoNetwork(), "SOURCE", "FACTORY", scenario
        );

        assertEquals(1350L, result.baselineMaximumFlowKgPerDay());
        assertEquals(700L, result.scenarioMaximumFlowKgPerDay());
        assertEquals(650L, result.throughputLossKgPerDay());
        assertEquals(-650L, result.throughputChangeKgPerDay());
        assertEquals(0L, result.scenarioCapacityKgPerDay());
    }

    @Test
    void increasingH2AloneDoesNotCreateSupplyThatUpstreamCannotProvide() {
        CapacityScenarioRequest scenario = new CapacityScenarioRequest(
                "Increase H2 factory capacity by 50 percent",
                CapacityScenarioType.INCREASE_BY_PERCENT,
                "H2",
                "FACTORY",
                null,
                50.0
        );

        CapacityScenarioResultResponse result = service.runScenario(
                demoNetwork(), "SOURCE", "FACTORY", scenario
        );

        assertEquals(1050L, result.scenarioCapacityKgPerDay());
        assertTrue(result.scenarioMaximumFlowKgPerDay() >= 1350L);
    }

    @Test
    void scenarioMustNotMutateOriginalNetwork() {
        FlowNetwork original = demoNetwork();

        FlowNetwork changed = service.withCapacityChange(
                original, "H1", "FACTORY", 0L
        );

        assertEquals(10, original.edges().size());
        assertEquals(9, changed.edges().size());
        assertTrue(original.edges().stream().anyMatch(e ->
                e.fromCode().equals("H1") && e.toCode().equals("FACTORY")));
    }

    @Test
    void exactRankingShouldPlaceHighImpactEdgesFirst() {
        BottleneckAnalysisResponse response = service.analyze(
                demoNetwork(), "SOURCE", "FACTORY", 10
        );

        List<BottleneckImpactResponse> ranking =
                response.exactClosureImpactRanking();

        for (int i = 1; i < ranking.size(); i++) {
            assertTrue(
                    ranking.get(i - 1).throughputLossIfClosedKgPerDay()
                            >= ranking.get(i).throughputLossIfClosedKgPerDay()
            );
        }
    }
}
