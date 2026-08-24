package com.agripulse.backend.dto;

import java.util.List;

public record GraphResponse(List<NodeResponse> nodes, List<EdgeResponse> edges) {
}
