package com.agripulse.dto;

public class FertilizerRequestCreateDto {

    private Long farmId;
    private String fertilizerType;
    private int requestedBags;
    private double benefitScore;
    private String urgencyLevel;

    public FertilizerRequestCreateDto() {
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public String getFertilizerType() {
        return fertilizerType;
    }

    public void setFertilizerType(String fertilizerType) {
        this.fertilizerType = fertilizerType;
    }

    public int getRequestedBags() {
        return requestedBags;
    }

    public void setRequestedBags(int requestedBags) {
        this.requestedBags = requestedBags;
    }

    public double getBenefitScore() {
        return benefitScore;
    }

    public void setBenefitScore(double benefitScore) {
        this.benefitScore = benefitScore;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
}