package com.agripulse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CapacityScenarioRequest(
        @NotBlank String name,
        @NotNull CapacityScenarioType type,
        @NotBlank String fromCode,
        @NotBlank String toCode,
        @PositiveOrZero Long newCapacityKgPerDay,
        @PositiveOrZero Double percent
) {
}
