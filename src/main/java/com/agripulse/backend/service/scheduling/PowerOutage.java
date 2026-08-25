package com.agripulse.backend.service.scheduling;

public class PowerOutage {

    private String outageId;
    private int startHour;
    private int endHour;

    public PowerOutage(String outageId, int startHour, int endHour) {
        this.outageId = outageId;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String getOutageId() {
        return outageId;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public boolean affectsHour(int hour) {
        return hour >= startHour && hour < endHour;
    }
}