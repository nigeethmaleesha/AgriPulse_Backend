package com.agripulse.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateEdgeRequest(
        @NotBlank String fromCode,
        @NotBlank String toCode,
        @Positive long capacityKgPerDay,
        Boolean active,
        @Size(max = 160) String label
) {
}
