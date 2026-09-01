package com.agripulse.routing;

import com.agripulse.dto.DispatchRequestDto;
import com.agripulse.dto.DispatchResponseDto;
import com.agripulse.model.HarvestBatch;
import com.agripulse.model.RoadEdge;
import com.agripulse.model.entity.HarvestBatchEntity;
import com.agripulse.model.entity.Road;
import com.agripulse.repository.CollectionPointRepository;
import com.agripulse.repository.HarvestBatchRepository;
import com.agripulse.repository.RoadRepository;
import com.agripulse.service.routing.DijkstraService;
import com.agripulse.service.routing.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchServiceTest {

    private DispatchService dispatchService;
    private CollectionPointRepository collectionPointRepository;
    private RoadRepository roadRepository;
    private HarvestBatchRepository harvestBatchRepository;

    @BeforeEach
    void setUp() {
        DijkstraService dijkstraService = new DijkstraService();
        collectionPointRepository = mock(CollectionPointRepository.class);
        roadRepository = mock(RoadRepository.class);
        harvestBatchRepository = mock(HarvestBatchRepository.class);

        dispatchService = new DispatchService(
                dijkstraService,
                collectionPointRepository,
                roadRepository,
                harvestBatchRepository
        );
    }

    @Test
    @DisplayName("Should select highest priority target when accessible")
    void testHighestPriorityReachableTargetSelection() {
        HarvestBatch batch1 = new HarvestBatch("BATCH-001", "CP_ALPHA", 75.0, true);
        HarvestBatch batch2 = new HarvestBatch("BATCH-002", "CP_BETA", 95.0, true);
        HarvestBatch batch3 = new HarvestBatch("BATCH-003", "CP_GAMMA", 85.0, true);

        List<HarvestBatch> batches = Arrays.asList(batch1, batch2, batch3);

        Map<String, List<RoadEdge>> graph = new HashMap<>();
        graph.put("TRUCK_DEPOT", Arrays.asList(
                new RoadEdge("CP_ALPHA", 10.0, 1.0, 1.0, false, true),
                new RoadEdge("CP_BETA", 20.0, 1.0, 1.0, false, true),
                new RoadEdge("CP_GAMMA", 15.0, 1.0, 1.0, false, true)
        ));

        DispatchRequestDto request = new DispatchRequestDto("TRUCK_DEPOT", batches, graph);

        DispatchResponseDto response = dispatchService.calculateDispatchRoute(request);

        assertNotNull(response);
        assertEquals("BATCH-002", response.getSelectedBatchId());
        assertEquals("CP_BETA", response.getTargetCollectionPoint());
        assertEquals(95.0, response.getPriorityScore());
        assertEquals(20.0, response.getTotalRouteCost(), 0.001);
        assertEquals(Arrays.asList("TRUCK_DEPOT", "CP_BETA"), response.getRecommendedPath());
    }

    @Test
    @DisplayName("Should calculate dynamic route using mock JPA repository open roads and ready batches")
    void testCalculateNextRouteFromDatabase() {
        // Mock DB open roads
        Road r1 = new Road(1L, "C1", "C2", 5.0, 1.0, 1.0, false, true, 1000.0);
        Road r2 = new Road(2L, "C2", "C6", 10.0, 1.2, 1.1, true, true, 1000.0);
        when(roadRepository.findByIsOpenTrue()).thenReturn(Arrays.asList(r1, r2));

        // Mock DB READY harvest batches: B-102 (C6, priority 95.0)
        HarvestBatchEntity batchEntity = new HarvestBatchEntity("B-102", "C6", 500.0, 95.0, "READY", LocalDateTime.now());
        when(harvestBatchRepository.findByStatus("READY")).thenReturn(Collections.singletonList(batchEntity));

        DispatchResponseDto response = dispatchService.calculateNextRouteFromDatabase("C1");

        assertNotNull(response);
        assertEquals("B-102", response.getSelectedBatchId());
        assertEquals("C6", response.getTargetCollectionPoint());
        assertEquals(95.0, response.getPriorityScore());
        assertEquals(Arrays.asList("C1", "C2", "C6"), response.getRecommendedPath());
        // Effective cost: C1->C2 = 5.0 * 1.0 * 1.0 * 1.0 = 5.0; C2->C6 = 10.0 * 1.2 * 1.1 * 1.5 = 19.8. Total = 24.8
        assertEquals(24.8, response.getTotalRouteCost(), 0.001);
    }
}
