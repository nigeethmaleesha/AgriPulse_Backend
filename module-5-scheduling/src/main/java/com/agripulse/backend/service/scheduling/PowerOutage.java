package com.agripulse.backend.service.scheduling;

/**
 * A recurring daily power outage window (24-hour clock). No task may start
 * or be in progress during an hour covered by an outage.
 */
public record PowerOutage(
        String outageId,
        int startHour,
        int endHour
) {
    public PowerOutage {
        if (outageId == null || outageId.isBlank()) {
            throw new IllegalArgumentException("outageId cannot be blank");
        }
        if (startHour < 0 || endHour > 24 || endHour <= startHour) {
            throw new IllegalArgumentException("outage hours must satisfy 0 <= startHour < endHour <= 24");
        }
    }

    public boolean affectsHourOfDay(int hourOfDay) {
        return hourOfDay >= startHour && hourOfDay < endHour;
    }
}
