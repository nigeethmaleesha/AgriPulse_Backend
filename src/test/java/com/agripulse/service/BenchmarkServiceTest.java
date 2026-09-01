package com.agripulse.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.agripulse.dto.AlgorithmComparisonDto;
import com.agripulse.service.allocation.BenchmarkService;
import com.agripulse.service.allocation.FertilizerAllocationService;
import com.agripulse.service.allocation.FractionalKnapsackService;
import com.agripulse.service.allocation.GreedyPriorityAllocationService;

class BenchmarkServiceTest {

    @Test
    void testRunFullBenchmark_GeneratesNineComparisonResults() {
        FertilizerAllocationService knapsackService = new FertilizerAllocationService();
        FractionalKnapsackService fractionalService = new FractionalKnapsackService();
        GreedyPriorityAllocationService greedyService = new GreedyPriorityAllocationService();

        BenchmarkService benchmarkService = new BenchmarkService(knapsackService, fractionalService, greedyService);

        List<AlgorithmComparisonDto> results = benchmarkService.runFullBenchmark();

        assertNotNull(results);
        // 3 algorithms x 3 dataset sizes (20, 200, 2000) = 9 total benchmark results
        assertEquals(9, results.size());

        // Verify dataset sizes 20, 200, 2000 are present
        assertEquals(20, results.get(0).getDatasetSize());
        assertEquals(200, results.get(3).getDatasetSize());
        assertEquals(2000, results.get(6).getDatasetSize());
    }
}
