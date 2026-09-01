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
@CrossOrigin(origins = "*")
@RequestMapping("/api/pumps")
public class PumpController {

    private final PumpAllocationService pumpAllocationService;

    @Autowired
    public PumpController(PumpAllocationService pumpAllocationService) {
        this.pumpAllocationService = pumpAllocationService;
    }

    /**
     * BENCHMARK ENDPOINT: Runs all 3 algorithms on synthetic tea farm datasets
     * Small (20), Medium (200), Large (2000) as specified in PDSA Blueprint Task 2B
     */
    @GetMapping("/benchmark")
    public ResponseEntity<List<PumpAllocationResultDto>> runPumpBenchmark(
            @RequestParam(defaultValue = "2000") int numberOfFarms,
            @RequestParam(defaultValue = "50") int availablePumps) {

        List<PumpRequest> dummyRequests = generateDummyRequests(numberOfFarms);
        List<PumpAllocationResultDto> benchmarkResults = new ArrayList<>();

        // 1. Max-Heap Priority Queue (Primary Algorithm - O(N log K))
        long startTime1 = System.currentTimeMillis();
        List<PumpRequest> heapResult = pumpAllocationService.allocatePumpsWithMaxHeap(new ArrayList<>(dummyRequests), availablePumps);
        long time1 = System.currentTimeMillis() - startTime1;
        benchmarkResults.add(new PumpAllocationResultDto("Max-Heap (Primary)", availablePumps, heapResult.size(), time1, heapResult));

        // 2. Full Sort (TimSort / Merge Sort Comparison - O(N log N))
        long startTime2 = System.currentTimeMillis();
        List<PumpRequest> sortResult = pumpAllocationService.allocatePumpsWithSort(new ArrayList<>(dummyRequests), availablePumps);
        long time2 = System.currentTimeMillis() - startTime2;
        benchmarkResults.add(new PumpAllocationResultDto("Full Sort (Comparison)", availablePumps, sortResult.size(), time2, sortResult));

        // 3. Simple Greedy Baseline (FCFS First-Come-First-Served - O(N))
        long startTime3 = System.currentTimeMillis();
        List<PumpRequest> greedyResult = pumpAllocationService.allocatePumpsGreedyBaseline(new ArrayList<>(dummyRequests), availablePumps);
        long time3 = System.currentTimeMillis() - startTime3;
        benchmarkResults.add(new PumpAllocationResultDto("Greedy Baseline", availablePumps, greedyResult.size(), time3, greedyResult));

        return ResponseEntity.ok(benchmarkResults);
    }

    private List<PumpRequest> generateDummyRequests(int count) {
        List<PumpRequest> list = new ArrayList<>();
        Random random = new Random(42L); // Fixed seed so results are reproducible
        String[] regions = {"Kandy", "Nuwara Eliya", "Ratnapura", "Matara", "Badulla"};
        String[] estateTypes = {"Green Valley", "Highland Tea Estate", "Valley View Farm", "Sunrise Smallholding", "Mist Mountain Plot"};

        for (long i = 1; i <= count; i++) {
            double score = Math.round((random.nextDouble() * 100) * 100.0) / 100.0;
            boolean eligible = random.nextDouble() > 0.1; // 90% eligible
            String region = regions[(int) (i % regions.length)];
            String farmName = estateTypes[(int) (i % estateTypes.length)] + " #" + i;
            double landSize = Math.round((0.5 + random.nextDouble() * 9.5) * 10.0) / 10.0;
            double deficiency = Math.round((random.nextDouble() * 100) * 10.0) / 10.0;

            list.add(new PumpRequest(
                i,
                farmName,
                region,
                "Tea",
                landSize,
                deficiency,
                score,
                eligible
            ));
        }
        return list;
    }
}