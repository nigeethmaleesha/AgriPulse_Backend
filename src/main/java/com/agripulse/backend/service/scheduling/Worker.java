package com.agripulse.backend.service.scheduling;

public class Worker {

    private String workerId;
    private String workerName;
    private String shift;
    private int maxWorkingHours;
    private int remainingWorkingHours;

    public Worker(
            String workerId,
            String workerName,
            String shift,
            int maxWorkingHours) {

        this.workerId = workerId;
        this.workerName = workerName;
        this.shift = shift;
        this.maxWorkingHours = maxWorkingHours;
        this.remainingWorkingHours = maxWorkingHours;
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getShift() {
        return shift;
    }

    public int getMaxWorkingHours() {
        return maxWorkingHours;
    }

    public int getRemainingWorkingHours() {
        return remainingWorkingHours;
    }

    public boolean canWork(int hours) {
        return remainingWorkingHours >= hours;
    }

    public void assignHours(int hours) {

        if (!canWork(hours)) {
            throw new IllegalArgumentException(
                    "Worker does not have enough remaining working hours"
            );
        }

        remainingWorkingHours -= hours;
    }
}