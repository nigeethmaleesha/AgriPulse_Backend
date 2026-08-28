package com.agripulse.model;

public class PumpRequest {


    private final Long farmId;
    private final double priorityScore;
    private final boolean isEligible;

    public PumpRequest(Long farmId, double priorityScore, boolean isEligible) {
        this.farmId = farmId;
        this.priorityScore = priorityScore;
        this.isEligible = isEligible;
    }

    public Long getFarmId() { return farmId; }
    public double getPriorityScore() { return priorityScore; }
    public boolean isEligible() { return isEligible; }
}