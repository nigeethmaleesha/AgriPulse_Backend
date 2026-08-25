package com.agripulse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResponseDto {

    private String selectedBatchId;
    private String targetCollectionPoint;
    private double priorityScore;
    private List<String> recommendedPath;
    private double totalRouteCost;
    private String statusMessage;

    public String getSelectedBatchId() {
        return selectedBatchId;
    }

    public void setSelectedBatchId(String selectedBatchId) {
        this.selectedBatchId = selectedBatchId;
    }

    public String getTargetCollectionPoint() {
        return targetCollectionPoint;
    }

    public void setTargetCollectionPoint(String targetCollectionPoint) {
        this.targetCollectionPoint = targetCollectionPoint;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public List<String> getRecommendedPath() {
        return recommendedPath;
    }

    public void setRecommendedPath(List<String> recommendedPath) {
        this.recommendedPath = recommendedPath;
    }

    public double getTotalRouteCost() {
        return totalRouteCost;
    }

    public void setTotalRouteCost(double totalRouteCost) {
        this.totalRouteCost = totalRouteCost;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
