package com.agripulse.backend.service.scheduling;

public class Machine {

    private String machineId;
    private String machineName;
    private int processingCapacity;
    private boolean available;

    public Machine(String machineId, String machineName,
                   int processingCapacity, boolean available) {

        this.machineId = machineId;
        this.machineName = machineName;
        this.processingCapacity = processingCapacity;
        this.available = available;
    }

    public String getMachineId() {
        return machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public int getProcessingCapacity() {
        return processingCapacity;
    }

    public boolean isAvailable() {
        return available;
    }
}