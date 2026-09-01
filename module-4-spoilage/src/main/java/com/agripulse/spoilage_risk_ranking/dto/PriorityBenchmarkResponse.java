package com.agripulse.spoilage_risk_ranking.dto;

import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;

import java.util.List;

public class PriorityBenchmarkResponse {
    private final List<AlgorithmTestResult> results;
    private final String note;

    public PriorityBenchmarkResponse(List<AlgorithmTestResult> results, String note) {
        this.results = results;
        this.note = note;
    }

    public List<AlgorithmTestResult> getResults() {
        return results;
    }

    public String getNote() {
        return note;
    }
}
