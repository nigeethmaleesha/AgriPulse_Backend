package com.agripulse.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateEdgeRequest(
        @Positive long capacityKgPerDay,
        @NotNull Boolean active,
        @Size(max = 160) String label
) {
}
