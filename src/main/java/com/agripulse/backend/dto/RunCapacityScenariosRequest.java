package com.agripulse.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RunCapacityScenariosRequest(
        @NotBlank String sourceCode,
        @NotBlank String sinkCode,
        @NotEmpty List<@Valid CapacityScenarioRequest> scenarios
) {
    public RunCapacityScenariosRequest {
        scenarios = scenarios == null ? null : List.copyOf(scenarios);
    }
}
