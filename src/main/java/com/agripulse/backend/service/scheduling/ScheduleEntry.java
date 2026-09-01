package com.agripulse.backend.service.scheduling;

/** One task placed onto a worker and a machine for a start/end hour. */
public record ScheduleEntry(
        ProductionTask task,
        Worker worker,
        Machine machine,
        int startHour,
        int endHour
) {
}
