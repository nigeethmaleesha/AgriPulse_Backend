package com.agripulse;

import com.agripulse.model.PumpRequest;
import com.agripulse.service.allocation.PumpAllocationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestMyAlgorithms {
    public static void main(String[] args) {
        PumpAllocationService service = new PumpAllocationService();

        // Test all three sizes: 20, 200, and 2000
        int[] datasetSizes = {20, 200, 2000};

        for (int numberOfFarms : datasetSizes) {
            int availablePumps = (int) (numberOfFarms * 0.40); // 40% capacity constraint

            // Generate synthetic data
            List<PumpRequest> dummyRequests = new ArrayList<>();
            Random random = new Random(42L);
            for (long i = 1; i <= numberOfFarms; i++) {
                double priorityScore = Math.round((random.nextDouble() * 100) * 10.0) / 10.0;
                dummyRequests.add(new PumpRequest(i, priorityScore, true));
            }

            System.out.println("=== BENCHMARKING " + numberOfFarms + " FARMS (Pumps: " + availablePumps + ") ===");

            // 1. Test Max-Heap
            long startHeap = System.currentTimeMillis();
            List<PumpRequest> heapResult = service.allocatePumpsWithMaxHeap(new ArrayList<>(dummyRequests), availablePumps);
            long endHeap = System.currentTimeMillis();
            printMetrics("Max-Heap (Priority Queue)", heapResult, availablePumps, numberOfFarms, endHeap - startHeap);

            // 2. Test Full Sort (TimSort)
            long startSort = System.currentTimeMillis();
            List<PumpRequest> sortResult = service.allocatePumpsWithSort(new ArrayList<>(dummyRequests), availablePumps);
            long endSort = System.currentTimeMillis();
            printMetrics("Full Sort (TimSort)", sortResult, availablePumps, numberOfFarms, endSort - startSort);

            // 3. Test Greedy Baseline
            long startGreedy = System.currentTimeMillis();
            List<PumpRequest> greedyResult = service.allocatePumpsGreedyBaseline(new ArrayList<>(dummyRequests), availablePumps);
            long endGreedy = System.currentTimeMillis();
            printMetrics("Greedy / FCFS", greedyResult, availablePumps, numberOfFarms, endGreedy - startGreedy);

            System.out.println();
        }
    }

    private static void printMetrics(String algoName, List<PumpRequest> allocated, int pumpsAvailable, int totalFarms, long timeMs) {
        double totalScore = allocated.stream().mapToDouble(PumpRequest::getPriorityScore).sum();
        double roundedScore = Math.round(totalScore * 10.0) / 10.0;
        System.out.println(algoName + " -> Score: " + roundedScore + " | Allocated: " + allocated.size() + "/" + totalFarms + " | Time: " + timeMs + " ms");
    }
}