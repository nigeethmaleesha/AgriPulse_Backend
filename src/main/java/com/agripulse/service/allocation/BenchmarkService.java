package com.agripulse.service.allocation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agripulse.dto.AlgorithmComparisonDto;
import com.agripulse.dto.AllocationResultDto;
import com.agripulse.model.FertilizerRequest;
import com.agripulse.util.FertilizerDataGenerator;

/**
 * Runs 0/1 Knapsack DP, Fractional Knapsack, and Greedy Priority Allocation
 * on the same synthetic datasets (20 / 200 / 2000 requests, per the AgriPulse
 * blueprint's Task 2 testing plan) and records comparable metrics for the report.
 */
@Service
public class BenchmarkService {

    private final FertilizerAllocationService knapsackService;
    private final FractionalKnapsackService fractionalService;
    private final GreedyPriorityAllocationService greedyService;

    @Autowired
    public BenchmarkService(FertilizerAllocationService knapsackService,
                             FractionalKnapsackService fractionalService,
                             GreedyPriorityAllocationService greedyService) {
        this.knapsackService = knapsackService;
        this.fractionalService = fractionalService;
        this.greedyService = greedyService;
    }

    /**
     * Runs all three algorithms across the standard test sizes (20, 200, 2000).
     * Capacity is set to 40% of total requested bags for each dataset, so the
     * allocation problem is meaningfully constrained (not trivially "allocate everything").
     */
    public List<AlgorithmComparisonDto> runFullBenchmark() {
        int[] datasetSizes = {20, 200, 2000};
        List<AlgorithmComparisonDto> results = new ArrayList<>();

        for (int size : datasetSizes) {
            // Fresh copies per algorithm, since services mutate status/allocatedBags in place
            List<FertilizerRequest> dataForKnapsack = FertilizerDataGenerator.generate(size);
            List<FertilizerRequest> dataForFractional = FertilizerDataGenerator.generate(size);
            List<FertilizerRequest> dataForGreedy = FertilizerDataGenerator.generate(size);

            int totalRequestedBags = dataForKnapsack.stream()
                    .mapToInt(FertilizerRequest::getRequestedBags)
                    .sum();
            int capacity = (int) (totalRequestedBags * 0.4);

            AllocationResultDto knapsackResult = knapsackService.allocate(dataForKnapsack, capacity);
            AllocationResultDto fractionalResult = fractionalService.allocate(dataForFractional, capacity);
            AllocationResultDto greedyResult = greedyService.allocate(dataForGreedy, capacity);

            results.add(new AlgorithmComparisonDto("0/1 Knapsack (DP)", size, knapsackResult));
            results.add(new AlgorithmComparisonDto("Fractional Knapsack (Greedy)", size, fractionalResult));
            results.add(new AlgorithmComparisonDto("Greedy Priority Allocation", size, greedyResult));
        }

        return results;
    }
}