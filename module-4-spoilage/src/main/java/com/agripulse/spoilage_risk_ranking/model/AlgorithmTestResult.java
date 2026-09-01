package com.agripulse.spoilage_risk_ranking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * One row per benchmark run. Used to build the comparison
 * tables and charts in your individual report - real measured
 * numbers, not estimates.
 */
@Entity
@Table(name = "algorithm_test_results")
public class AlgorithmTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String module;             // e.g. "spoilage_ranking"
    private String algorithm;          // "bubble" | "insertion" | "merge"
    private Integer inputSize;
    private Double executionTimeMs;
    private Double memoryMb;
    private String solutionMetric;     // optional notes
    private LocalDateTime runAt = LocalDateTime.now();

    public AlgorithmTestResult() {
    }

    public AlgorithmTestResult(String module, String algorithm, int inputSize,
                               double executionTimeMs, double memoryMb) {
        this.module = module;
        this.algorithm = algorithm;
        this.inputSize = inputSize;
        this.executionTimeMs = executionTimeMs;
        this.memoryMb = memoryMb;
    }

    // --- Getters and setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getInputSize() {
        return inputSize;
    }

    public void setInputSize(Integer inputSize) {
        this.inputSize = inputSize;
    }

    public Double getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Double executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public Double getMemoryMb() {
        return memoryMb;
    }

    public void setMemoryMb(Double memoryMb) {
        this.memoryMb = memoryMb;
    }

    public String getSolutionMetric() {
        return solutionMetric;
    }

    public void setSolutionMetric(String solutionMetric) {
        this.solutionMetric = solutionMetric;
    }

    public LocalDateTime getRunAt() {
        return runAt;
    }

    public void setRunAt(LocalDateTime runAt) {
        this.runAt = runAt;
    }
}
