package com.agripulse.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RunCapacityScenarioRequest(
        @NotBlank String sourceCode,
        @NotBlank String sinkCode,
        @NotNull @Valid CapacityScenarioRequest scenario
) {
}
