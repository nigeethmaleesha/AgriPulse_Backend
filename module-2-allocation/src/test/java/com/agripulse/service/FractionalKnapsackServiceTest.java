package com.agripulse.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.Farm;
import com.agripulse.model.FertilizerRequest;
import com.agripulse.service.allocation.FractionalKnapsackService;

class FractionalKnapsackServiceTest {

    private FractionalKnapsackService service;
    private Farm sampleFarm;

    @BeforeEach
    void setUp() {
        service = new FractionalKnapsackService();
        sampleFarm = new Farm("Test Farm", "0712345678", "Kandy", "Tea", 5.0);
    }

    @Test
    void testAllocate_NullOrEmptyRequests_ThrowsException() {
        assertThrows(FertilizerAllocationException.class, () -> service.allocate(null, 50));
    }

    @Test
    void testAllocate_PartialBagFractionalAllocation() {
        // Request 1: 10 bags, score 100 (ratio 10.0) -> takes full 10 bags (score 100)
        // Request 2: 20 bags, score 120 (ratio 6.0) -> takes remaining 5 bags (5/20 * 120 = 30)
        // Total capacity: 15 bags
        // Expected benefit: 100 + 30 = 130.0
        FertilizerRequest r1 = new FertilizerRequest(sampleFarm, "Urea", 10, 100.0, "HIGH");
        FertilizerRequest r2 = new FertilizerRequest(sampleFarm, "NPK", 20, 120.0, "MEDIUM");

        List<FertilizerRequest> requests = List.of(r1, r2);

        AllocationResultDto result = service.allocate(requests, 15);

        assertNotNull(result);
        assertEquals(2, result.getAllocatedRequests().size());
        assertEquals(0, result.getRejectedRequests().size());
        assertEquals(130.0, result.getTotalBenefitAchieved(), 0.001);
        assertEquals(15, result.getUsedCapacity());

        // Verify partial allocated bags on r2
        assertEquals(10, r1.getAllocatedBags());
        assertEquals(5, r2.getAllocatedBags());
    }

    @Test
    void testAllocate_ZeroBagsGuard() {
        FertilizerRequest r1 = new FertilizerRequest(sampleFarm, "Urea", 0, 50.0, "LOW");
        FertilizerRequest r2 = new FertilizerRequest(sampleFarm, "NPK", 10, 40.0, "HIGH");

        AllocationResultDto result = service.allocate(List.of(r1, r2), 10);

        assertNotNull(result);
        assertEquals(40.0, result.getTotalBenefitAchieved(), 0.001);
    }
}
