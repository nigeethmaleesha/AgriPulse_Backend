package com.agripulse.model;

public class PumpRequest {

    private Long farmId;
    private String farmName;
    private String region;
    private String cropType;
    private Double landSize;
    private Double waterDeficiency;
    private double priorityScore;
    private boolean isEligible = true;

    public PumpRequest() {
    }

    public PumpRequest(Long farmId, double priorityScore, boolean isEligible) {
        this.farmId = farmId;
        this.farmName = "Farm #" + farmId;
        this.region = "Kandy";
        this.cropType = "Tea";
        this.landSize = 3.5;
        this.waterDeficiency = priorityScore;
        this.priorityScore = priorityScore;
        this.isEligible = isEligible;
    }

    public PumpRequest(Long farmId, String farmName, String region, String cropType,
                       Double landSize, Double waterDeficiency, double priorityScore, boolean isEligible) {
        this.farmId = farmId;
        this.farmName = farmName;
        this.region = region;
        this.cropType = cropType;
        this.landSize = landSize;
        this.waterDeficiency = waterDeficiency;
        this.priorityScore = priorityScore;
        this.isEligible = isEligible;
    }

    public Long getFarmId() { return farmId; }
    public void setFarmId(Long farmId) { this.farmId = farmId; }

    public String getFarmName() { return farmName; }
    public void setFarmName(String farmName) { this.farmName = farmName; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }

    public Double getLandSize() { return landSize; }
    public void setLandSize(Double landSize) { this.landSize = landSize; }

    public Double getWaterDeficiency() { return waterDeficiency; }
    public void setWaterDeficiency(Double waterDeficiency) { this.waterDeficiency = waterDeficiency; }

    public double getPriorityScore() { return priorityScore; }
    public void setPriorityScore(double priorityScore) { this.priorityScore = priorityScore; }

    public boolean isEligible() { return isEligible; }
    public void setEligible(boolean eligible) { isEligible = eligible; }
}