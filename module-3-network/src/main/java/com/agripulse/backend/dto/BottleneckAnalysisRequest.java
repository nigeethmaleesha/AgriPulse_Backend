package com.agripulse.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record BottleneckAnalysisRequest(
        @NotBlank String sourceCode,
        @NotBlank String sinkCode,
        @Min(1) @Max(100) Integer topN
) {
}
