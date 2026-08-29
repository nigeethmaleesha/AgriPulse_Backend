package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulingBenchmarkService {

    public static final List<Integer> PRESET_SIZES = List.of(10, 30, 80);
    private static final long SEED = 99L;

    private final SyntheticSchedulingGenerator generator;
    private final FactorySchedulingService schedulingService;

    public SchedulingBenchmarkService(SyntheticSchedulingGenerator generator, FactorySchedulingService schedulingService) {
        this.generator = generator;
        this.schedulingService = schedulingService;
    }

    public List<SchedulingBenchmarkRow> run(List<Integer> sizes) {
        List<Integer> effectiveSizes = (sizes == null || sizes.isEmpty()) ? PRESET_SIZES : sizes;
        List<SchedulingBenchmarkRow> rows = new ArrayList<>();

        for (int size : effectiveSizes) {
            SyntheticSchedulingGenerator.Scenario scenario = generator.generate(size, SEED);
            ScheduleComparisonResult comparison = schedulingService.compare(
                    scenario.tasks(), scenario.workers(), scenario.machines(), scenario.outages());

            rows.add(new SchedulingBenchmarkRow(
                    size,
                    scenario.workers().size(),
                    scenario.machines().size(),
                    comparison.geneticResult().executionTimeMs(),
                    comparison.annealingResult().executionTimeMs(),
                    comparison.geneticResult().totalPriorityValue(),
                    comparison.annealingResult().totalPriorityValue(),
                    comparison.geneticResult().tasksScheduled(),
                    comparison.annealingResult().tasksScheduled(),
                    comparison.differencePercent()
            ));
        }
        return rows;
    }
}
