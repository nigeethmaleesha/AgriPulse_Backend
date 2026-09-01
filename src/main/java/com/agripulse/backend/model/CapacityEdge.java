package com.agripulse.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "capacity_edges", uniqueConstraints = {
        @UniqueConstraint(name = "uk_capacity_edge_pair", columnNames = {"from_node_id", "to_node_id"})
})
public class CapacityEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_node_id", nullable = false)
    private SupplyNode fromNode;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_node_id", nullable = false)
    private SupplyNode toNode;

    @Column(name = "capacity_kg_per_day", nullable = false)
    private long capacityKgPerDay;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 160)
    private String label;

    public CapacityEdge() {
    }

    public CapacityEdge(SupplyNode fromNode, SupplyNode toNode, long capacityKgPerDay, boolean active, String label) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.capacityKgPerDay = capacityKgPerDay;
        this.active = active;
        this.label = label;
    }

    public Long getId() { return id; }
    public SupplyNode getFromNode() { return fromNode; }
    public void setFromNode(SupplyNode fromNode) { this.fromNode = fromNode; }
    public SupplyNode getToNode() { return toNode; }
    public void setToNode(SupplyNode toNode) { this.toNode = toNode; }
    public long getCapacityKgPerDay() { return capacityKgPerDay; }
    public void setCapacityKgPerDay(long capacityKgPerDay) { this.capacityKgPerDay = capacityKgPerDay; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
