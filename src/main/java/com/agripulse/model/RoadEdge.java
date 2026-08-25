package com.agripulse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadEdge {

    private String toNode;
    private double distance;
    private double inclineFactor;
    private double qualityPenalty;
    private boolean monsoonAffected;
    private boolean open;

    /**
     * Computes effective edge cost according to:
     * If open == false -> Double.POSITIVE_INFINITY
     * Effective Cost = distance * inclineFactor * qualityPenalty * (monsoonAffected ? 1.5 : 1.0)
     */
    public double getEffectiveCost() {
        if (!open) {
            return Double.POSITIVE_INFINITY;
        }
        double monsoonMultiplier = monsoonAffected ? 1.5 : 1.0;
        return distance * inclineFactor * qualityPenalty * monsoonMultiplier;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getInclineFactor() {
        return inclineFactor;
    }

    public void setInclineFactor(double inclineFactor) {
        this.inclineFactor = inclineFactor;
    }

    public double getQualityPenalty() {
        return qualityPenalty;
    }

    public void setQualityPenalty(double qualityPenalty) {
        this.qualityPenalty = qualityPenalty;
    }

    public boolean isMonsoonAffected() {
        return monsoonAffected;
    }

    public void setMonsoonAffected(boolean monsoonAffected) {
        this.monsoonAffected = monsoonAffected;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoadEdge roadEdge = (RoadEdge) o;
        return Double.compare(roadEdge.distance, distance) == 0 &&
                Double.compare(roadEdge.inclineFactor, inclineFactor) == 0 &&
                Double.compare(roadEdge.qualityPenalty, qualityPenalty) == 0 &&
                monsoonAffected == roadEdge.monsoonAffected &&
                open == roadEdge.open &&
                Objects.equals(toNode, roadEdge.toNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toNode, distance, inclineFactor, qualityPenalty, monsoonAffected, open);
    }
}
