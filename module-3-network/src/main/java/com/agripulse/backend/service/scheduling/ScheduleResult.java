package com.agripulse.backend.service.scheduling;

import java.util.List;

/** Result of decoding one candidate task order into a concrete schedule. */
public record ScheduleResult(
        String method,
        List<ScheduleEntry> scheduledEntries,
        List<ProductionTask> unscheduledTasks,
        long totalPriorityValue,
        int tasksScheduled,
        int tasksTotal,
        long executionTimeNanos,
        double executionTimeMs
) {
    public ScheduleResult {
        scheduledEntries = List.copyOf(scheduledEntries);
        unscheduledTasks = List.copyOf(unscheduledTasks);
    }

    public static ScheduleResult of(String method, List<ScheduleEntry> scheduledEntries,
                                     List<ProductionTask> unscheduledTasks, long totalPriorityValue,
                                     int tasksTotal, long executionTimeNanos) {
        return new ScheduleResult(method, scheduledEntries, unscheduledTasks, totalPriorityValue,
                scheduledEntries.size(), tasksTotal, executionTimeNanos, executionTimeNanos / 1_000_000.0);
    }
}
