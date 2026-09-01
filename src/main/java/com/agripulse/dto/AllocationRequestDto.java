package com.agripulse.dto;

public class AllocationRequestDto {

    private int totalCapacity; // total fertilizer bags available for this allocation round

    public AllocationRequestDto() {
    }

    public AllocationRequestDto(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }
}