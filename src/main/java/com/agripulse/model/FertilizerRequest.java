package com.agripulse.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fertilizer_request")
public class FertilizerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private String fertilizerType;

    @Column(nullable = false)
    private int requestedBags;

    @Column(nullable = false)
    private double benefitScore;

    @Column(nullable = false)
    private String urgencyLevel = "MEDIUM";

    @Column(nullable = false)
    private String status = "PENDING";

    @Column
    private int allocatedBags = 0;

    @Column(nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FertilizerRequest() {
    }

    public FertilizerRequest(Farm farm, String fertilizerType, int requestedBags,
                              double benefitScore, String urgencyLevel) {
        this.farm = farm;
        this.fertilizerType = fertilizerType;
        this.requestedBags = requestedBags;
        this.benefitScore = benefitScore;
        this.urgencyLevel = urgencyLevel;
        this.status = "PENDING";
        this.requestDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAllocatedBags() {
        return allocatedBags;
    }

    public void setAllocatedBags(int allocatedBags) {
        this.allocatedBags = allocatedBags;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}