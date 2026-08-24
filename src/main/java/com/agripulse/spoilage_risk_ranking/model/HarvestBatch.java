package com.agripulse.spoilage_risk_ranking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Maps directly to the harvest_batches table.
 * Spring Data JPA reads/writes this table for you -
 * you never write raw SQL for basic CRUD.
 */
@Entity
@Table(name = "harvest_batches")
public class HarvestBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long farmId;
    private Long collectionPointId;
    private Double quantity;
    private LocalDateTime harvestTime;
    private Double temperature;
    private Double humidity;
    private Double riskScore;   // set by RiskRankingService, not by the client
    private String status = "ready";

    public HarvestBatch() {
    }

    // --- Getters and setters (JPA and JSON serialization need these) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
    }

    public Long getCollectionPointId() {
        return collectionPointId;
    }

    public void setCollectionPointId(Long collectionPointId) {
        this.collectionPointId = collectionPointId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getHarvestTime() {
        return harvestTime;
    }

    public void setHarvestTime(LocalDateTime harvestTime) {
        this.harvestTime = harvestTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Helper used by the risk formula: how many hours ago the batch
     * was harvested. Longer waiting time = higher spoilage risk.
     */
    @Transient
    public double hoursSinceHarvest() {
        if (harvestTime == null) return 0;
        return java.time.Duration.between(harvestTime, LocalDateTime.now()).toMinutes() / 60.0;
    }
}