package com.agripulse.repository;

import com.agripulse.model.entity.HarvestBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HarvestBatchRepository extends JpaRepository<HarvestBatchEntity, String> {

    List<HarvestBatchEntity> findByStatus(String status);
}
