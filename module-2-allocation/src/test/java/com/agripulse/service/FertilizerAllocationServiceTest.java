package com.agripulse.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.Farm;
import com.agripulse.model.FertilizerRequest;
import com.agripulse.service.allocation.FertilizerAllocationService;

class FertilizerAllocationServiceTest {

    private FertilizerAllocationService service;
    private Farm sampleFarm;

    @BeforeEach
    void setUp() {
        service = new FertilizerAllocationService();
        sampleFarm = new Farm("Test Farm", "0712345678", "Kandy", "Tea", 5.0);
    }

    @Test
    void testAllocate_NullOrEmptyRequests_ThrowsException() {
        assertThrows(FertilizerAllocationException.class, () -> service.allocate(null, 50));
        assertThrows(FertilizerAllocationException.class, () -> service.allocate(new ArrayList<>(), 50));
    }

    @Test
    void testAllocate_NegativeCapacity_ThrowsException() {
        List<FertilizerRequest> requests = List.of(
                new FertilizerRequest(sampleFarm, "Urea", 10, 50.0, "HIGH")
        );
        assertThrows(FertilizerAllocationException.class, () -> service.allocate(requests, -1));
    }

    @Test
    void testAllocate_ZeroCapacity_RejectsAll() {
        List<FertilizerRequest> requests = List.of(
                new FertilizerRequest(sampleFarm, "Urea", 10, 50.0, "HIGH"),
                new FertilizerRequest(sampleFarm, "NPK", 20, 80.0, "MEDIUM")
        );

        AllocationResultDto result = service.allocate(requests, 0);

        assertNotNull(result);
        assertEquals(0, result.getAllocatedRequests().size());
        assertEquals(2, result.getRejectedRequests().size());
        assertEquals(0.0, result.getTotalBenefitAchieved());
        assertEquals(0, result.getUsedCapacity());
    }

    @Test
    void testAllocate_OptimalKnapsackSelection() {
        // Request 1: 20 bags, score 60 (ratio 3.0)
        // Request 2: 30 bags, score 100 (ratio 3.33)
        // Request 3: 10 bags, score 50 (ratio 5.0)
        // Capacity: 40
        // Optimal selection: Request 2 (30 bags) + Request 3 (10 bags) = 40 bags, score 150.0
        // (Greedy by score alone would pick R2 (30 bags) and then couldn't fit R1 (20) -> score 100)
        FertilizerRequest r1 = new FertilizerRequest(sampleFarm, "Urea", 20, 60.0, "MEDIUM");
        FertilizerRequest r2 = new FertilizerRequest(sampleFarm, "NPK", 30, 100.0, "HIGH");
        FertilizerRequest r3 = new FertilizerRequest(sampleFarm, "Compost", 10, 50.0, "LOW");

        List<FertilizerRequest> requests = List.of(r1, r2, r3);

        AllocationResultDto result = service.allocate(requests, 40);

        assertEquals(2, result.getAllocatedRequests().size());
        assertEquals(1, result.getRejectedRequests().size());
        assertEquals(150.0, result.getTotalBenefitAchieved(), 0.001);
        assertEquals(40, result.getUsedCapacity());
        assertTrue(result.getAllocatedRequests().contains(r2));
        assertTrue(result.getAllocatedRequests().contains(r3));
    }

    @Test
    void testAllocate_SufficientCapacity_AllocatesAll() {
        FertilizerRequest r1 = new FertilizerRequest(sampleFarm, "Urea", 10, 40.0, "LOW");
        FertilizerRequest r2 = new FertilizerRequest(sampleFarm, "NPK", 15, 60.0, "HIGH");

        List<FertilizerRequest> requests = List.of(r1, r2);

        AllocationResultDto result = service.allocate(requests, 50);

        assertEquals(2, result.getAllocatedRequests().size());
        assertEquals(0, result.getRejectedRequests().size());
        assertEquals(100.0, result.getTotalBenefitAchieved(), 0.001);
        assertEquals(25, result.getUsedCapacity());
    }
}
