package com.agripulse.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "harvest_batches")
public class HarvestBatchEntity {

    @Id
    private String id;

    private String collectionPointId;
    private double quantity;
    private double priorityScore;
    private String status; // "READY", "COLLECTED", "IN_TRANSIT"
    private LocalDateTime harvestTime;

    public HarvestBatchEntity() {
    }

    public HarvestBatchEntity(String id, String collectionPointId, double quantity, double priorityScore, String status, LocalDateTime harvestTime) {
        this.id = id;
        this.collectionPointId = collectionPointId;
        this.quantity = quantity;
        this.priorityScore = priorityScore;
        this.status = status;
        this.harvestTime = harvestTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectionPointId() {
        return collectionPointId;
    }

    public void setCollectionPointId(String collectionPointId) {
        this.collectionPointId = collectionPointId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getHarvestTime() {
        return harvestTime;
    }

    public void setHarvestTime(LocalDateTime harvestTime) {
        this.harvestTime = harvestTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HarvestBatchEntity that = (HarvestBatchEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
