package com.agripulse.backend.repository;

import com.agripulse.backend.model.CapacityEdge;
import com.agripulse.backend.model.SupplyNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapacityEdgeRepository extends JpaRepository<CapacityEdge, Long> {
    List<CapacityEdge> findAllByOrderByIdAsc();
    boolean existsByFromNodeAndToNode(SupplyNode fromNode, SupplyNode toNode);
}
