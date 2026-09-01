package com.agripulse.backend.service.scheduling;

public class ScheduleEntry {

    private ProductionTask task;
    private Worker worker;
    private Machine machine;
    private int startHour;
    private int endHour;

    public ScheduleEntry(ProductionTask task,
                         Worker worker,
                         Machine machine,
                         int startHour,
                         int endHour) {

        this.task = task;
        this.worker = worker;
        this.machine = machine;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public ProductionTask getTask() {
        return task;
    }

    public Worker getWorker() {
        return worker;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
}