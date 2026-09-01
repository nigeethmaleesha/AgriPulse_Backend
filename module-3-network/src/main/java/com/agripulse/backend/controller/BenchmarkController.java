package com.agripulse.backend.controller;

import com.agripulse.backend.dto.BenchmarkRequest;
import com.agripulse.backend.dto.BenchmarkResponse;
import com.agripulse.backend.dto.BottleneckBenchmarkResponse;
import com.agripulse.backend.service.network.BenchmarkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Shared benchmark API for Module 3 (Members 5 and 6). */
@RestController
@RequestMapping("/api/network/benchmark")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    /** Member 5: benchmark the Ford-Fulkerson max-flow engine. */
    @PostMapping
    public BenchmarkResponse runMaxFlowBenchmark(
            @Valid @RequestBody BenchmarkRequest request
    ) {
        return benchmarkService.run(request);
    }

    /** Member 6: benchmark the bottleneck-analysis candidate methods. */
    @PostMapping("/bottlenecks")
    public BottleneckBenchmarkResponse runBottleneckBenchmark(
            @Valid @RequestBody BenchmarkRequest request
    ) {
        return benchmarkService.runBottleneck(request);
    }

    @GetMapping("/presets")
    public List<Map<String, Integer>> presets() {
        return List.of(
                Map.of("nodeCount", 20, "edgeCount", 30),
                Map.of("nodeCount", 100, "edgeCount", 250),
                Map.of("nodeCount", 500, "edgeCount", 2000)
        );
    }
}
