package com.agripulse.backend.service.scheduling;

/**
 * A worker available for a shift, with a fixed daily working-hour limit
 * (labor constraint) that a schedule must not exceed.
 */
public record Worker(
        String workerId,
        String workerName,
        String shift,
        int maxWorkingHours
) {
    public Worker {
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("workerId cannot be blank");
        }
        if (maxWorkingHours <= 0) {
            throw new IllegalArgumentException("maxWorkingHours must be greater than 0");
        }
    }
}
