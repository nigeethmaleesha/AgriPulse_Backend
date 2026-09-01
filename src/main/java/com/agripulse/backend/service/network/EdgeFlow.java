package com.agripulse.backend.service.network;

public record EdgeFlow(
        String fromCode,
        String toCode,
        long capacityKgPerDay,
        long flowKgPerDay,
        long residualCapacityKgPerDay
) {
    public double utilizationPercent() {
        if (capacityKgPerDay == 0) {
            return 0.0;
        }
        return (flowKgPerDay * 100.0) / capacityKgPerDay;
    }
}
