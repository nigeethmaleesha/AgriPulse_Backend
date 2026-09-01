package com.agripulse.backend.service.scheduling;

/**
 * One factory processing task (e.g. a tea batch on the rolling machinery)
 * waiting to be scheduled into a worker shift and a machine slot.
 */
public record ProductionTask(
        String taskId,
        String taskName,
        int processingTimeHours,
        int priority
) {
    public ProductionTask {
        if (taskId == null || taskId.isBlank()) {
            throw new IllegalArgumentException("taskId cannot be blank");
        }
        if (processingTimeHours <= 0) {
            throw new IllegalArgumentException("processingTimeHours must be greater than 0");
        }
        if (priority <= 0) {
            throw new IllegalArgumentException("priority must be greater than 0");
        }
    }
}
