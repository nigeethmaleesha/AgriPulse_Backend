package com.agripulse.dto;

import java.util.List;

public class DispatchResponseDto {

    private String selectedBatchId;
    private String targetCollectionPoint;
    private double priorityScore;
    private List<String> recommendedPath;
    private double totalRouteCost;
    private String statusMessage;

    public DispatchResponseDto() {
    }

    public DispatchResponseDto(String selectedBatchId, String targetCollectionPoint, double priorityScore,
                               List<String> recommendedPath, double totalRouteCost, String statusMessage) {
        this.selectedBatchId = selectedBatchId;
        this.targetCollectionPoint = targetCollectionPoint;
        this.priorityScore = priorityScore;
        this.recommendedPath = recommendedPath;
        this.totalRouteCost = totalRouteCost;
        this.statusMessage = statusMessage;
    }

    public static DispatchResponseDtoBuilder builder() {
        return new DispatchResponseDtoBuilder();
    }

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

    public static class DispatchResponseDtoBuilder {
        private String selectedBatchId;
        private String targetCollectionPoint;
        private double priorityScore;
        private List<String> recommendedPath;
        private double totalRouteCost;
        private String statusMessage;

        public DispatchResponseDtoBuilder selectedBatchId(String selectedBatchId) {
            this.selectedBatchId = selectedBatchId;
            return this;
        }

        public DispatchResponseDtoBuilder targetCollectionPoint(String targetCollectionPoint) {
            this.targetCollectionPoint = targetCollectionPoint;
            return this;
        }

        public DispatchResponseDtoBuilder priorityScore(double priorityScore) {
            this.priorityScore = priorityScore;
            return this;
        }

        public DispatchResponseDtoBuilder recommendedPath(List<String> recommendedPath) {
            this.recommendedPath = recommendedPath;
            return this;
        }

        public DispatchResponseDtoBuilder totalRouteCost(double totalRouteCost) {
            this.totalRouteCost = totalRouteCost;
            return this;
        }

        public DispatchResponseDtoBuilder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public DispatchResponseDto build() {
            return new DispatchResponseDto(selectedBatchId, targetCollectionPoint, priorityScore,
                    recommendedPath, totalRouteCost, statusMessage);
        }
    }
}
