package com.agripulse.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "algorithm_test_results")
public class AlgorithmTestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String module;

    @Column(nullable = false, length = 80)
    private String algorithm;

    @Column(name = "input_size", nullable = false)
    private int inputSize;

    @Column(name = "edge_count", nullable = false)
    private int edgeCount;

    @Column(name = "execution_time_ms", nullable = false)
    private double executionTimeMs;

    @Column(name = "memory_mb", nullable = false)
    private double memoryMb;

    @Column(name = "solution_metric", nullable = false)
    private long solutionMetric;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public AlgorithmTestResult() {
    }

    public AlgorithmTestResult(String module, String algorithm, int inputSize, int edgeCount,
                               double executionTimeMs, double memoryMb, long solutionMetric) {
        this.module = module;
        this.algorithm = algorithm;
        this.inputSize = inputSize;
        this.edgeCount = edgeCount;
        this.executionTimeMs = executionTimeMs;
        this.memoryMb = memoryMb;
        this.solutionMetric = solutionMetric;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getModule() { return module; }
    public String getAlgorithm() { return algorithm; }
    public int getInputSize() { return inputSize; }
    public int getEdgeCount() { return edgeCount; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public double getMemoryMb() { return memoryMb; }
    public long getSolutionMetric() { return solutionMetric; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
