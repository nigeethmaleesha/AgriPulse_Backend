package com.agripulse.spoilage_risk_ranking.dto;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;

public class PriorityQueueStatusResponse {
    private final int size;
    private final boolean empty;
    private final HarvestBatch highestRiskBatch;

    public PriorityQueueStatusResponse(int size, boolean empty, HarvestBatch highestRiskBatch) {
        this.size = size;
        this.empty = empty;
        this.highestRiskBatch = highestRiskBatch;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return empty;
    }

    public HarvestBatch getHighestRiskBatch() {
        return highestRiskBatch;
    }
}
