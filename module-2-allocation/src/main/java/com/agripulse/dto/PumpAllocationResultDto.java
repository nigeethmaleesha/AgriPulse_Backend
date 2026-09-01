package com.agripulse.dto;

import com.agripulse.model.PumpRequest;
import java.util.List;

public class PumpAllocationResultDto {

    // Added 'final' to lock these variables and clear the warnings!
    private final String algorithmUsed;
    private final int totalPumpsAvailable;
    private final int pumpsAllocated;
    private final long executionTimeMillis;
    private final List<PumpRequest> allocatedFarms;

    public PumpAllocationResultDto(String algorithmUsed, int totalPumpsAvailable, int pumpsAllocated, long executionTimeMillis, List<PumpRequest> allocatedFarms) {
        this.algorithmUsed = algorithmUsed;
        this.totalPumpsAvailable = totalPumpsAvailable;
        this.pumpsAllocated = pumpsAllocated;
        this.executionTimeMillis = executionTimeMillis;
        this.allocatedFarms = allocatedFarms;
    }

    public String getAlgorithmUsed() { return algorithmUsed; }
    public int getTotalPumpsAvailable() { return totalPumpsAvailable; }
    public int getPumpsAllocated() { return pumpsAllocated; }
    public long getExecutionTimeMillis() { return executionTimeMillis; }
    public List<PumpRequest> getAllocatedFarms() { return allocatedFarms; }
}