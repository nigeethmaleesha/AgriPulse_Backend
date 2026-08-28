package com.agripulse.backend.controller;

import com.agripulse.backend.dto.BenchmarkRequest;
import com.agripulse.backend.dto.BenchmarkResponse;
import com.agripulse.backend.service.network.BenchmarkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network/benchmark")
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    public BenchmarkController(BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @PostMapping
    public BenchmarkResponse run(@Valid @RequestBody BenchmarkRequest request) {
        return benchmarkService.run(request);
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
