package com.agripulse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadStatusUpdateDto {

    private boolean isOpen;
    private boolean monsoonStatus;

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
