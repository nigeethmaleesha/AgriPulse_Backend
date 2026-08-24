package com.agripulse.backend.repository;

import com.agripulse.backend.model.AlgorithmTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlgorithmTestResultRepository extends JpaRepository<AlgorithmTestResult, Long> {
    List<AlgorithmTestResult> findTop100ByOrderByCreatedAtDesc();
}
