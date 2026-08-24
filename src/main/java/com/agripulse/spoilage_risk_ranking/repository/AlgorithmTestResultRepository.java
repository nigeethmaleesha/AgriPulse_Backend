package com.agripulse.spoilage_risk_ranking.repository;

import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlgorithmTestResultRepository extends JpaRepository<AlgorithmTestResult, Long> {

    List<AlgorithmTestResult> findByModule(String module);
}