package com.agripulse.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roads")
public class Road {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromPointId;
    private String toPointId;
    private double distance;
    private double incline;
    private double roadQuality;
    private boolean monsoonStatus;
    private boolean isOpen;
    private double capacityKgPerDay;

    public Road() {
    }

    public Road(Long id, String fromPointId, String toPointId, double distance, double incline, double roadQuality, boolean monsoonStatus, boolean isOpen, double capacityKgPerDay) {
        this.id = id;
        this.fromPointId = fromPointId;
        this.toPointId = toPointId;
        this.distance = distance;
        this.incline = incline;
        this.roadQuality = roadQuality;
        this.monsoonStatus = monsoonStatus;
        this.isOpen = isOpen;
        this.capacityKgPerDay = capacityKgPerDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromPointId() {
        return fromPointId;
    }

    public void setFromPointId(String fromPointId) {
        this.fromPointId = fromPointId;
    }

    public String getToPointId() {
        return toPointId;
    }

    public void setToPointId(String toPointId) {
        this.toPointId = toPointId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getIncline() {
        return incline;
    }

    public void setIncline(double incline) {
        this.incline = incline;
    }

    public double getRoadQuality() {
        return roadQuality;
    }

    public void setRoadQuality(double roadQuality) {
        this.roadQuality = roadQuality;
    }

    public boolean isMonsoonStatus() {
        return monsoonStatus;
    }

    public void setMonsoonStatus(boolean monsoonStatus) {
        this.monsoonStatus = monsoonStatus;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean open) {
        isOpen = open;
    }

    public double getCapacityKgPerDay() {
        return capacityKgPerDay;
    }

    public void setCapacityKgPerDay(double capacityKgPerDay) {
        this.capacityKgPerDay = capacityKgPerDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Road road = (Road) o;
        return Objects.equals(id, road.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
