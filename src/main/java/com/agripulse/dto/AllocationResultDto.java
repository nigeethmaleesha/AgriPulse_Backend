package com.agripulse.dto;

import java.util.List;

import com.agripulse.model.FertilizerRequest;

public class AllocationResultDto {

    private List<FertilizerRequest> allocatedRequests;
    private List<FertilizerRequest> rejectedRequests;

    private int totalCapacity;
    private int usedCapacity;
    private int totalRequestedBags;      // sum of requestedBags across ALL requests considered
    private int totalAllocatedBags;      // sum of bags actually allocated (same as usedCapacity, kept explicit for clarity)

    private int totalRequestsCount;      // how many requests were considered
    private int allocatedRequestsCount;  // how many were approved
    private int rejectedRequestsCount;   // how many were rejected

    private double totalBenefitAchieved;
    private double capacityUtilizationPercent; // usedCapacity / totalCapacity * 100
    private double demandFulfillmentPercent;   // usedCapacity / totalRequestedBags * 100

    private long executionTimeMillis;

    public AllocationResultDto() {
    }

    public AllocationResultDto(List<FertilizerRequest> allocatedRequests,
                                List<FertilizerRequest> rejectedRequests,
                                int totalCapacity,
                                int usedCapacity,
                                int totalRequestedBags,
                                double totalBenefitAchieved,
                                long executionTimeMillis) {
        this.allocatedRequests = allocatedRequests;
        this.rejectedRequests = rejectedRequests;
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
        this.totalAllocatedBags = usedCapacity;
        this.totalRequestedBags = totalRequestedBags;
        this.totalRequestsCount = allocatedRequests.size() + rejectedRequests.size();
        this.allocatedRequestsCount = allocatedRequests.size();
        this.rejectedRequestsCount = rejectedRequests.size();
        this.totalBenefitAchieved = totalBenefitAchieved;
        this.executionTimeMillis = executionTimeMillis;

        this.capacityUtilizationPercent = totalCapacity == 0 ? 0.0 :
                Math.round((usedCapacity * 100.0 / totalCapacity) * 100.0) / 100.0;

        this.demandFulfillmentPercent = totalRequestedBags == 0 ? 0.0 :
                Math.round((usedCapacity * 100.0 / totalRequestedBags) * 100.0) / 100.0;
    }

    // Getters and setters

    public List<FertilizerRequest> getAllocatedRequests() {
        return allocatedRequests;
    }

    public void setAllocatedRequests(List<FertilizerRequest> allocatedRequests) {
        this.allocatedRequests = allocatedRequests;
    }

    public List<FertilizerRequest> getRejectedRequests() {
        return rejectedRequests;
    }

    public void setRejectedRequests(List<FertilizerRequest> rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
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

    public int getTotalRequestedBags() {
        return totalRequestedBags;
    }

    public void setTotalRequestedBags(int totalRequestedBags) {
        this.totalRequestedBags = totalRequestedBags;
    }

    public int getTotalAllocatedBags() {
        return totalAllocatedBags;
    }

    public void setTotalAllocatedBags(int totalAllocatedBags) {
        this.totalAllocatedBags = totalAllocatedBags;
    }

    public int getTotalRequestsCount() {
        return totalRequestsCount;
    }

    public void setTotalRequestsCount(int totalRequestsCount) {
        this.totalRequestsCount = totalRequestsCount;
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

    public long getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(long executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }
}