package com.agripulse.backend.dto;

import com.agripulse.backend.model.NodeType;

public record NodeResponse(Long id, String code, String name, NodeType nodeType, boolean active) {
}
