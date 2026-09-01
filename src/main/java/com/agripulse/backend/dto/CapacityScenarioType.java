package com.agripulse.backend.dto;

/**
 * Member 6 in-memory capacity changes.
 * No scenario permanently modifies the database graph.
 */
public enum CapacityScenarioType {
    CLOSE_LINK,
    REDUCE_BY_PERCENT,
    INCREASE_BY_PERCENT,
    SET_CAPACITY
}
