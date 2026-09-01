package com.agripulse.backend.dto;

import com.agripulse.backend.service.scheduling.Machine;
import com.agripulse.backend.service.scheduling.PowerOutage;
import com.agripulse.backend.service.scheduling.ProductionTask;
import com.agripulse.backend.service.scheduling.Worker;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FactoryScheduleRequest(
        @NotEmpty List<@NotNull ProductionTask> tasks,
        @NotEmpty List<@NotNull Worker> workers,
        @NotEmpty List<@NotNull Machine> machines,
        List<@NotNull PowerOutage> outages
) {
    public List<PowerOutage> outages() {
        return outages == null ? List.of() : outages;
    }
}
