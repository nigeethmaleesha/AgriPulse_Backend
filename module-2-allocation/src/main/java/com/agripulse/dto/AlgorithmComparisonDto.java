package com.agripulse.dto;

public class AlgorithmComparisonDto {

    private String algorithmName;
    private int datasetSize;
    private int totalCapacity;
    private int usedCapacity;
    private double totalBenefitAchieved;
    private double capacityUtilizationPercent;
    private double demandFulfillmentPercent;
    private int allocatedRequestsCount;
    private int rejectedRequestsCount;
    private long executionTimeMillis;

    public AlgorithmComparisonDto() {
    }

    public AlgorithmComparisonDto(String algorithmName, int datasetSize, AllocationResultDto result) {
        this.algorithmName = algorithmName;
        this.datasetSize = datasetSize;
        this.totalCapacity = result.getTotalCapacity();
        this.usedCapacity = result.getUsedCapacity();
        this.totalBenefitAchieved = result.getTotalBenefitAchieved();
        this.capacityUtilizationPercent = result.getCapacityUtilizationPercent();
        this.demandFulfillmentPercent = result.getDemandFulfillmentPercent();
        this.allocatedRequestsCount = result.getAllocatedRequestsCount();
        this.rejectedRequestsCount = result.getRejectedRequestsCount();
        this.executionTimeMillis = result.getExecutionTimeMillis();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public int getDatasetSize() {
        return datasetSize;
    }

    public void setDatasetSize(int datasetSize) {
        this.datasetSize = datasetSize;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(int usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public double getTotalBenefitAchieved() {
        return totalBenefitAchieved;
    }

    public void setTotalBenefitAchieved(double totalBenefitAchieved) {
        this.totalBenefitAchieved = totalBenefitAchieved;
    }

    public double getCapacityUtilizationPercent() {
        return capacityUtilizationPercent;
    }

    public void setCapacityUtilizationPercent(double capacityUtilizationPercent) {
        this.capacityUtilizationPercent = capacityUtilizationPercent;
    }

    public double getDemandFulfillmentPercent() {
        return demandFulfillmentPercent;
    }

    public void setDemandFulfillmentPercent(double demandFulfillmentPercent) {
        this.demandFulfillmentPercent = demandFulfillmentPercent;
    }

    public int getAllocatedRequestsCount() {
        return allocatedRequestsCount;
    }

    public void setAllocatedRequestsCount(int allocatedRequestsCount) {
        this.allocatedRequestsCount = allocatedRequestsCount;
    }

    public int getRejectedRequestsCount() {
        return rejectedRequestsCount;
    }

    public void setRejectedRequestsCount(int rejectedRequestsCount) {
        this.rejectedRequestsCount = rejectedRequestsCount;
    }

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }
}