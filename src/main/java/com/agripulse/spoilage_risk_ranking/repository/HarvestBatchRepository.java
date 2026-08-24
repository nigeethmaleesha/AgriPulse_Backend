package com.agripulse.spoilage_risk_ranking.repository;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Extending JpaRepository gives you save(), findAll(), findById(),
 * deleteById() etc. with NO SQL written by hand.
 * You only add extra methods here if you need a custom query.
 */
public interface HarvestBatchRepository extends JpaRepository<HarvestBatch, Long> {

    // Spring Data JPA reads this method name and builds the SQL
    // automatically: "find all where status = ?"
    List<HarvestBatch> findByStatus(String status);
}