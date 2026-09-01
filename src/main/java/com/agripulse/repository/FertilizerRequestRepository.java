package com.agripulse.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agripulse.model.FertilizerRequest;

public interface FertilizerRequestRepository extends JpaRepository<FertilizerRequest, Long> {

    List<FertilizerRequest> findByStatus(String status);

    List<FertilizerRequest> findByFarmId(Long farmId);
}