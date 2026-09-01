package com.agripulse.model;

import java.util.Objects;

public class HarvestBatch implements Comparable<HarvestBatch> {

    private String batchId;
    private String collectionPointId;
    private double priorityScore;
    private boolean ready;

    public HarvestBatch() {
    }

    public HarvestBatch(String batchId, String collectionPointId, double priorityScore, boolean ready) {
        this.batchId = batchId;
        this.collectionPointId = collectionPointId;
        this.priorityScore = priorityScore;
        this.ready = ready;
    }

    /**
     * Max-Heap ordering based on priorityScore (descending order).
     * If priorityScores are equal, tie-breaks using batchId for deterministic ordering.
     */
    @Override
    public int compareTo(HarvestBatch other) {
        if (other == null) {
            return -1;
        }
        int scoreCompare = Double.compare(other.priorityScore, this.priorityScore);
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        if (this.batchId == null && other.batchId == null) {
            return 0;
        }
        if (this.batchId == null) {
            return 1;
        }
        if (other.batchId == null) {
            return -1;
        }
        return this.batchId.compareTo(other.batchId);
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getCollectionPointId() {
        return collectionPointId;
    }

    public void setCollectionPointId(String collectionPointId) {
        this.collectionPointId = collectionPointId;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HarvestBatch that = (HarvestBatch) o;
        return Double.compare(that.priorityScore, priorityScore) == 0 &&
                ready == that.ready &&
                Objects.equals(batchId, that.batchId) &&
                Objects.equals(collectionPointId, that.collectionPointId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId, collectionPointId, priorityScore, ready);
    }
}
