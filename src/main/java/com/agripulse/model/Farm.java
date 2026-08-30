package com.agripulse.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "farm")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String farmName;

    @Column(nullable = false, unique = true)
    private String contactNumber;

    @Column(nullable = false)
    private String region;

    @Column
    private String cropType;

    @Column
    private Double landSize;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "farm"})
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private List<FertilizerRequest> fertilizerRequests = new ArrayList<>();

    public Farm() {
    }

    public Farm(String farmName, String contactNumber, String region, String cropType, Double landSize) {
        this.farmName = farmName;
        this.contactNumber = contactNumber;
        this.region = region;
        this.cropType = cropType;
        this.landSize = landSize;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<FertilizerRequest> getFertilizerRequests() {
        return fertilizerRequests;
    }

    public void setFertilizerRequests(List<FertilizerRequest> fertilizerRequests) {
        this.fertilizerRequests = fertilizerRequests;
    }
}