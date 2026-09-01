package com.agripulse.backend.repository;

import com.agripulse.backend.model.NodeType;
import com.agripulse.backend.model.SupplyNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplyNodeRepository extends JpaRepository<SupplyNode, Long> {
    Optional<SupplyNode> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    Optional<SupplyNode> findFirstByNodeType(NodeType nodeType);
    List<SupplyNode> findAllByOrderByIdAsc();
}
