package com.agripulse.backend.service.scheduling;

public class ProductionTask {

    private String taskId;
    private String taskName;
    private int processingTime;
    private int priority;

    public ProductionTask(String taskId, String taskName,
                           int processingTime, int priority) {

        this.taskId = taskId;
        this.taskName = taskName;
        this.processingTime = processingTime;
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getPriority() {
        return priority;
    }
}