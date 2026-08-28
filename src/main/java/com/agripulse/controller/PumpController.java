package com.agripulse.controller;

import com.agripulse.dto.PumpAllocationResultDto;
import com.agripulse.model.PumpRequest;
import com.agripulse.service.allocation.PumpAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/pumps")
public class PumpController {

    private final PumpAllocationService pumpAllocationService;

    @Autowired
    public PumpController(PumpAllocationService pumpAllocationService) {
        this.pumpAllocationService = pumpAllocationService;
    }

    /**
     * BENCHMARK ENDPOINT: Runs all 3 algorithms on a synthetic dataset
     *
     */
    @GetMapping("/benchmark")
    public ResponseEntity<List<PumpAllocationResultDto>> runPumpBenchmark(
            @RequestParam(defaultValue = "2000") int numberOfFarms,
            @RequestParam(defaultValue = "50") int availablePumps) {

        // 1. Generate Fake Data for testing
        List<PumpRequest> dummyRequests = generateDummyRequests(numberOfFarms);
        List<PumpAllocationResultDto> benchmarkResults = new ArrayList<>();

        // 2. Test Max-Heap ( primary algorithm)
        long startTime1 = System.currentTimeMillis();
        List<PumpRequest> heapResult = pumpAllocationService.allocatePumpsWithMaxHeap(new ArrayList<>(dummyRequests), availablePumps);
        long time1 = System.currentTimeMillis() - startTime1;
        benchmarkResults.add(new PumpAllocationResultDto("Max-Heap (Primary)", availablePumps, heapResult.size(), time1, heapResult));

        // 3. Test TimSort/Merge Sort (Comparison)
        long startTime2 = System.currentTimeMillis();
        List<PumpRequest> sortResult = pumpAllocationService.allocatePumpsWithSort(new ArrayList<>(dummyRequests), availablePumps);
        long time2 = System.currentTimeMillis() - startTime2;
        benchmarkResults.add(new PumpAllocationResultDto("Full Sort (Comparison)", availablePumps, sortResult.size(), time2, sortResult));

        // 4. Test Greedy (Baseline)
        long startTime3 = System.currentTimeMillis();
        List<PumpRequest> greedyResult = pumpAllocationService.allocatePumpsGreedyBaseline(new ArrayList<>(dummyRequests), availablePumps);
        long time3 = System.currentTimeMillis() - startTime3;
        benchmarkResults.add(new PumpAllocationResultDto("Greedy Baseline", availablePumps, greedyResult.size(), time3, greedyResult));

        return ResponseEntity.ok(benchmarkResults);
    }

    // Helper method to generate random farm data for your tests
    private List<PumpRequest> generateDummyRequests(int count) {
        List<PumpRequest> list = new ArrayList<>();
        Random random = new Random(42L); // Fixed seed so results are repeatable
        for (long i = 1; i <= count; i++) {
            double score = Math.round((random.nextDouble() * 100) * 100.0) / 100.0;
            boolean eligible = random.nextDouble() > 0.1; // 90% chance to be eligible
            list.add(new PumpRequest(i, score, eligible));
        }
        return list;
    }
}