package com.agripulse.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "supply_nodes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_supply_node_code", columnNames = "code")
})
public class SupplyNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 20)
    private NodeType nodeType;

    @Column(nullable = false)
    private boolean active = true;

    public SupplyNode() {
    }

    public SupplyNode(String code, String name, NodeType nodeType, boolean active) {
        this.code = code;
        this.name = name;
        this.nodeType = nodeType;
        this.active = active;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public NodeType getNodeType() { return nodeType; }
    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
