package com.agripulse.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RunMaxFlowRequest(
        @NotBlank String sourceCode,
        @NotBlank String sinkCode,
        Boolean saveBenchmark
) {
}
