package com.agripulse.dto;

public class RoadStatusUpdateDto {

    private boolean isOpen;
    private boolean monsoonStatus;

    public RoadStatusUpdateDto() {
    }

    public RoadStatusUpdateDto(boolean isOpen, boolean monsoonStatus) {
        this.isOpen = isOpen;
        this.monsoonStatus = monsoonStatus;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean open) {
        isOpen = open;
    }

    public boolean isMonsoonStatus() {
        return monsoonStatus;
    }

    public void setMonsoonStatus(boolean monsoonStatus) {
        this.monsoonStatus = monsoonStatus;
    }
}
