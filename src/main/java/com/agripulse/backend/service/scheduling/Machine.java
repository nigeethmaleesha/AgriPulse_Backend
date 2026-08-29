package com.agripulse.backend.service.scheduling;

/** A piece of factory rolling machinery that can process one task at a time. */
public record Machine(
        String machineId,
        String machineName,
        boolean available
) {
    public Machine {
        if (machineId == null || machineId.isBlank()) {
            throw new IllegalArgumentException("machineId cannot be blank");
        }
    }
}
