package com.agripulse.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Update request for a saved transport connection.
 *
 * fromCode/toCode are optional for backwards compatibility. When supplied,
 * the connection endpoints are changed after the same domain validation used
 * during creation (SOURCE -> FARM -> HUB -> FACTORY).
 */
public record UpdateEdgeRequest(
        String fromCode,
        String toCode,
        @Positive long capacityKgPerDay,
        @NotNull Boolean active,
        @Size(max = 160) String label
) {
}
