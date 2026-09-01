package com.agripulse.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.agripulse.model.PumpRequest;
import com.agripulse.service.allocation.PumpAllocationService;

class PumpAllocationServiceTest {

    private PumpAllocationService service;

    @BeforeEach
    void setUp() {
        service = new PumpAllocationService();
    }

    @Test
    void testAllocatePumpsWithMaxHeap_TopPrioritySelection() {
        PumpRequest r1 = new PumpRequest(1L, 45.0, true);
        PumpRequest r2 = new PumpRequest(2L, 95.0, true);
        PumpRequest r3 = new PumpRequest(3L, 80.0, true);
        PumpRequest r4 = new PumpRequest(4L, 99.0, false); // Ineligible

        List<PumpRequest> requests = List.of(r1, r2, r3, r4);

        // Allocate 2 pumps
        List<PumpRequest> allocated = service.allocatePumpsWithMaxHeap(requests, 2);

        assertNotNull(allocated);
        assertEquals(2, allocated.size());
        // Top eligible: r2 (95.0) and r3 (80.0)
        assertEquals(2L, allocated.get(0).getFarmId());
        assertEquals(3L, allocated.get(1).getFarmId());
    }

    @Test
    void testAllocatePumpsWithSort_MatchesHeapResults() {
        PumpRequest r1 = new PumpRequest(1L, 45.0, true);
        PumpRequest r2 = new PumpRequest(2L, 95.0, true);
        PumpRequest r3 = new PumpRequest(3L, 80.0, true);

        List<PumpRequest> requests = List.of(r1, r2, r3);

        List<PumpRequest> heapResult = service.allocatePumpsWithMaxHeap(requests, 2);
        List<PumpRequest> sortResult = service.allocatePumpsWithSort(requests, 2);

        assertEquals(heapResult.size(), sortResult.size());
        for (int i = 0; i < heapResult.size(); i++) {
            assertEquals(heapResult.get(i).getFarmId(), sortResult.get(i).getFarmId());
            assertEquals(heapResult.get(i).getPriorityScore(), sortResult.get(i).getPriorityScore());
        }
    }

    @Test
    void testAllocatePumps_NullOrZeroPumps_ReturnsEmptyList() {
        assertTrue(service.allocatePumpsWithMaxHeap(null, 5).isEmpty());
        assertTrue(service.allocatePumpsWithMaxHeap(List.of(), 5).isEmpty());
        assertTrue(service.allocatePumpsWithMaxHeap(List.of(new PumpRequest(1L, 50.0, true)), 0).isEmpty());
        assertTrue(service.allocatePumpsWithMaxHeap(List.of(new PumpRequest(1L, 50.0, true)), -1).isEmpty());
    }
}
