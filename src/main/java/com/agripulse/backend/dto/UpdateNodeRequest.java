package com.agripulse.backend.dto;

import com.agripulse.backend.model.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNodeRequest(
        @NotBlank @Size(max = 120) String name,
        @NotNull NodeType nodeType,
        @NotNull Boolean active
) {
}
