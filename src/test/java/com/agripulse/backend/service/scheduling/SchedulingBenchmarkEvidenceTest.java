package com.agripulse.backend.service.scheduling;

import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Prints real measured Genetic Algorithm vs Simulated Annealing evidence at
 * small/medium/large task counts (run with:
 * mvn -Dtest=SchedulingBenchmarkEvidenceTest test). Numbers used in the
 * technical report are copied from this test's console output.
 */
class SchedulingBenchmarkEvidenceTest {

    @Test
    void printBenchmarkTable() {
        SchedulingBenchmarkService benchmarkService = new SchedulingBenchmarkService(
                new SyntheticSchedulingGenerator(),
                new FactorySchedulingService(
                        new GeneticAlgorithmSchedulerService(new ScheduleDecoder()),
                        new SimulatedAnnealingSchedulerService(new ScheduleDecoder())
                )
        );

        List<SchedulingBenchmarkRow> rows = benchmarkService.run(SchedulingBenchmarkService.PRESET_SIZES);

        System.out.println();
        System.out.println("==================== FACTORY SCHEDULING BENCHMARK EVIDENCE (GA vs SA) ====================");
        System.out.printf("%-8s %-8s %-9s %-10s %-10s %-10s %-10s %-14s %-14s %-10s%n",
                "Tasks", "Workers", "Machines", "GATime(ms)", "SATime(ms)", "GAValue", "SAValue",
                "GAScheduled", "SAScheduled", "Diff(%)");
        for (SchedulingBenchmarkRow row : rows) {
            System.out.printf("%-8d %-8d %-9d %-10.3f %-10.3f %-10d %-10d %-14d %-14d %-10.2f%n",
                    row.taskCount(), row.workerCount(), row.machineCount(),
                    row.geneticExecutionTimeMs(), row.annealingExecutionTimeMs(),
                    row.geneticTotalValue(), row.annealingTotalValue(),
                    row.geneticTasksScheduled(), row.annealingTasksScheduled(), row.differencePercent());
        }
        System.out.println("=============================================================================================");
    }
}
