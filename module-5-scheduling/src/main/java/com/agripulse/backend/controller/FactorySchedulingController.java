package com.agripulse.backend.controller;

import com.agripulse.backend.dto.FactoryScheduleRequest;
import com.agripulse.backend.service.scheduling.FactorySchedulingService;
import com.agripulse.backend.service.scheduling.ScheduleComparisonResult;
import com.agripulse.backend.service.scheduling.ScheduleResult;
import com.agripulse.backend.service.scheduling.SchedulingBenchmarkRow;
import com.agripulse.backend.service.scheduling.SchedulingBenchmarkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Factory Processing & Worker Shift Scheduling.
 * Genetic Algorithm vs Simulated Annealing comparison.
 */
@RestController
@RequestMapping("/api/scheduling")
public class FactorySchedulingController {

    private final FactorySchedulingService schedulingService;
    private final SchedulingBenchmarkService benchmarkService;

    public FactorySchedulingController(FactorySchedulingService schedulingService,
                                        SchedulingBenchmarkService benchmarkService) {
        this.schedulingService = schedulingService;
        this.benchmarkService = benchmarkService;
    }

    @PostMapping("/genetic")
    public ScheduleResult runGenetic(@Valid @RequestBody FactoryScheduleRequest request) {
        return schedulingService.runGenetic(request.tasks(), request.workers(), request.machines(), request.outages());
    }

    @PostMapping("/annealing")
    public ScheduleResult runAnnealing(@Valid @RequestBody FactoryScheduleRequest request) {
        return schedulingService.runAnnealing(request.tasks(), request.workers(), request.machines(), request.outages());
    }

    @PostMapping("/compare")
    public ScheduleComparisonResult compare(@Valid @RequestBody FactoryScheduleRequest request) {
        return schedulingService.compare(request.tasks(), request.workers(), request.machines(), request.outages());
    }

    @GetMapping("/benchmark/presets")
    public List<Integer> benchmarkPresets() {
        return SchedulingBenchmarkService.PRESET_SIZES;
    }

    @PostMapping("/benchmark")
    public List<SchedulingBenchmarkRow> runBenchmark(@RequestParam(required = false) List<Integer> sizes) {
        return benchmarkService.run(sizes);
    }
}
