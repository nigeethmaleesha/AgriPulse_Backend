package com.agripulse.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.model.Farm;
import com.agripulse.model.FertilizerRequest;
import com.agripulse.service.allocation.GreedyPriorityAllocationService;

class GreedyPriorityAllocationServiceTest {

    private GreedyPriorityAllocationService service;
    private Farm sampleFarm;

    @BeforeEach
    void setUp() {
        service = new GreedyPriorityAllocationService();
        sampleFarm = new Farm("Test Farm", "0712345678", "Kandy", "Tea", 5.0);
    }

    @Test
    void testAllocate_GreedyByBenefitScore_SkipsNonFittingItems() {
        // Request 1: score 100, bags 30 -> fits in 40 capacity (10 capacity remaining)
        // Request 2: score 80, bags 20  -> 20 > 10, cannot fit -> REJECTED
        // Request 3: score 50, bags 10  -> 10 <= 10, fits! -> ALLOCATED
        FertilizerRequest r1 = new FertilizerRequest(sampleFarm, "NPK", 30, 100.0, "HIGH");
        FertilizerRequest r2 = new FertilizerRequest(sampleFarm, "Urea", 20, 80.0, "MEDIUM");
        FertilizerRequest r3 = new FertilizerRequest(sampleFarm, "Compost", 10, 50.0, "LOW");

        List<FertilizerRequest> requests = List.of(r1, r2, r3);

        AllocationResultDto result = service.allocate(requests, 40);

        assertNotNull(result);
        assertEquals(2, result.getAllocatedRequests().size());
        assertEquals(1, result.getRejectedRequests().size());
        assertEquals(150.0, result.getTotalBenefitAchieved(), 0.001);
        assertEquals(40, result.getUsedCapacity());
        assertTrue(result.getAllocatedRequests().contains(r1));
        assertTrue(result.getAllocatedRequests().contains(r3));
        assertTrue(result.getRejectedRequests().contains(r2));
    }
}
