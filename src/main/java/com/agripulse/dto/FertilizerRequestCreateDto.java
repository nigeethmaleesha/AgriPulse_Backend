package com.agripulse.dto;

public class FertilizerRequestCreateDto {

    private String farmName;
    private String contactNumber;

    // Only required if this is a brand-new farm (not yet registered)
    private String region;
    private String cropType;
    private Double landSize;

    private String fertilizerType;
    private int requestedBags;
    private double benefitScore;
    private String urgencyLevel;

    public FertilizerRequestCreateDto() {
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public Double getLandSize() {
        return landSize;
    }

    public void setLandSize(Double landSize) {
        this.landSize = landSize;
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