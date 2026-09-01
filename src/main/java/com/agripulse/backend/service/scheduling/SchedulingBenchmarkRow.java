package com.agripulse.backend.service.scheduling;

public record SchedulingBenchmarkRow(
        int taskCount,
        int workerCount,
        int machineCount,
        double geneticExecutionTimeMs,
        double annealingExecutionTimeMs,
        long geneticTotalValue,
        long annealingTotalValue,
        int geneticTasksScheduled,
        int annealingTasksScheduled,
        double differencePercent
) {
}
