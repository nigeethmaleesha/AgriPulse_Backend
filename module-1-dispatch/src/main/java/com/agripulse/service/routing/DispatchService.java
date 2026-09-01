package com.agripulse.service.routing;

import com.agripulse.dto.DispatchRequestDto;
import com.agripulse.dto.DispatchResponseDto;
import com.agripulse.dto.RoadStatusUpdateDto;
import com.agripulse.model.HarvestBatch;
import com.agripulse.model.RoadEdge;
import com.agripulse.model.entity.CollectionPoint;
import com.agripulse.model.entity.HarvestBatchEntity;
import com.agripulse.model.entity.Road;
import com.agripulse.repository.CollectionPointRepository;
import com.agripulse.repository.HarvestBatchRepository;
import com.agripulse.repository.RoadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DispatchService {

    private final DijkstraService dijkstraService;
    private final CollectionPointRepository collectionPointRepository;
    private final RoadRepository roadRepository;
    private final HarvestBatchRepository harvestBatchRepository;

    @Autowired
    public DispatchService(DijkstraService dijkstraService,
                           CollectionPointRepository collectionPointRepository,
                           RoadRepository roadRepository,
                           HarvestBatchRepository harvestBatchRepository) {
        this.dijkstraService = dijkstraService;
        this.collectionPointRepository = collectionPointRepository;
        this.roadRepository = roadRepository;
        this.harvestBatchRepository = harvestBatchRepository;
    }

    /**
     * In-memory route calculation method (for direct API payload evaluation).
     */
    public DispatchResponseDto calculateDispatchRoute(DispatchRequestDto request) {
        if (request == null) {
            return DispatchResponseDto.builder()
                    .statusMessage("Error: Request payload cannot be null")
                    .totalRouteCost(-1.0)
                    .build();
        }

        String truckStartNode = request.getTruckCurrentNode();
        List<HarvestBatch> batches = request.getReadyBatches();
        Map<String, List<RoadEdge>> roadGraph = request.getRoadGraph();

        return computeRouteForBatchesAndGraph(truckStartNode, batches, roadGraph);
    }

    /**
     * Database-backed dynamic route calculation:
     * Fetches open roads and READY harvest batches directly from PostgreSQL repositories.
     */
    @Transactional(readOnly = true)
    public DispatchResponseDto calculateNextRouteFromDatabase(String truckNode) {
        if (truckNode == null || truckNode.trim().isEmpty()) {
            return DispatchResponseDto.builder()
                    .statusMessage("Error: truckNode query parameter is required")
                    .totalRouteCost(-1.0)
                    .build();
        }

        // 1. Fetch open roads from DB & construct graph adjacency list
        List<Road> openRoads = roadRepository.findByIsOpenTrue();
        Map<String, List<RoadEdge>> graph = new HashMap<>();

        for (Road road : openRoads) {
            if (road != null && road.getFromPointId() != null && road.getToPointId() != null) {
                RoadEdge edge = new RoadEdge(
                        road.getToPointId(),
                        road.getDistance(),
                        road.getIncline(),
                        road.getRoadQuality(),
                        road.isMonsoonStatus(),
                        road.isOpen()
                );
                graph.computeIfAbsent(road.getFromPointId(), k -> new ArrayList<>()).add(edge);
            }
        }

        // 2. Fetch READY batches from DB & convert to domain HarvestBatch list
        List<HarvestBatchEntity> readyBatchEntities = harvestBatchRepository.findByStatus("READY");
        List<HarvestBatch> readyBatches = new ArrayList<>();

        for (HarvestBatchEntity entity : readyBatchEntities) {
            if (entity != null) {
                HarvestBatch domainBatch = new HarvestBatch(
                        entity.getId(),
                        entity.getCollectionPointId(),
                        entity.getPriorityScore(),
                        "READY".equalsIgnoreCase(entity.getStatus())
                );
                readyBatches.add(domainBatch);
            }
        }

        return computeRouteForBatchesAndGraph(truckNode, readyBatches, graph);
    }

    /**
     * Dynamic update for road closure or monsoon status in database.
     */
    @Transactional
    public Road updateRoadStatus(Long roadId, RoadStatusUpdateDto updateDto) {
        Road road = roadRepository.findById(roadId)
                .orElseThrow(() -> new IllegalArgumentException("Road not found with id: " + roadId));

        road.setIsOpen(updateDto.isOpen());
        road.setMonsoonStatus(updateDto.isMonsoonStatus());

        return roadRepository.save(road);
    }

    /**
     * Marks a harvest batch as "COLLECTED" in database.
     */
    @Transactional
    public HarvestBatchEntity markBatchAsCollected(String batchId) {
        HarvestBatchEntity batch = harvestBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Harvest batch not found with id: " + batchId));

        batch.setStatus("COLLECTED");
        return harvestBatchRepository.save(batch);
    }

    /**
     * Seeds initial database data matching nodes C1-C6, roads, and ready batches B-102, B-091.
     */
    @Transactional
    public String seedInitialData() {
        // Seed Collection Points C1 to C6
        List<CollectionPoint> points = Arrays.asList(
                new CollectionPoint("C1", "Main Factory / Central Depot", 6.9271, 79.8612),
                new CollectionPoint("C2", "Valley Collection Point 2", 6.9320, 79.8700),
                new CollectionPoint("C3", "Hills Junction Point 3", 6.9400, 79.8800),
                new CollectionPoint("C4", "Highland Farm Point 4", 6.9500, 79.8900),
                new CollectionPoint("C5", "River Pass Point 5", 6.9600, 79.9000),
                new CollectionPoint("C6", "Mountain Peak Collection Point 6", 6.9700, 79.9100)
        );
        collectionPointRepository.saveAll(points);

        // Seed Roads (bidirectional / directed connections between C1-C6)
        List<Road> roads = Arrays.asList(
                new Road(null, "C1", "C2", 5.0, 1.0, 1.0, false, true, 2000.0),
                new Road(null, "C2", "C3", 8.0, 1.2, 1.1, true, true, 1500.0),
                new Road(null, "C3", "C4", 6.0, 1.1, 1.0, false, true, 1800.0),
                new Road(null, "C4", "C6", 10.0, 1.5, 1.3, true, true, 1200.0),
                new Road(null, "C2", "C5", 12.0, 1.3, 1.2, false, true, 1400.0),
                new Road(null, "C5", "C6", 7.0, 1.4, 1.1, false, true, 1600.0),
                new Road(null, "C1", "C3", 14.0, 1.2, 1.0, false, true, 2500.0)
        );
        roadRepository.saveAll(roads);

        // Seed Ready Batches: B-102 (Critical at C6, priority 95.0) and B-091 (High at C4, priority 75.0)
        List<HarvestBatchEntity> batches = Arrays.asList(
                new HarvestBatchEntity("B-102", "C6", 500.0, 95.0, "READY", LocalDateTime.now()),
                new HarvestBatchEntity("B-091", "C4", 350.0, 75.0, "READY", LocalDateTime.now().minusHours(2))
        );
        harvestBatchRepository.saveAll(batches);

        return "Initial seed data successfully populated (Nodes C1-C6, 7 Roads, Batches B-102 and B-091).";
    }

    /**
     * Core routing helper using Max-Heap for batch selection and Min-Heap Dijkstra for route calculation.
     */
    private DispatchResponseDto computeRouteForBatchesAndGraph(String truckStartNode,
                                                               List<HarvestBatch> batches,
                                                               Map<String, List<RoadEdge>> roadGraph) {
        if (truckStartNode == null || truckStartNode.trim().isEmpty()) {
            return DispatchResponseDto.builder()
                    .statusMessage("Error: Truck current node is required")
                    .totalRouteCost(-1.0)
                    .build();
        }

        if (batches == null || batches.isEmpty()) {
            return DispatchResponseDto.builder()
                    .statusMessage("No harvest batches available for dispatch")
                    .totalRouteCost(-1.0)
                    .recommendedPath(Collections.emptyList())
                    .build();
        }

        // 1. Max-Heap for priority target selection
        PriorityQueue<HarvestBatch> maxHeap = new PriorityQueue<>();

        for (HarvestBatch batch : batches) {
            if (batch != null && batch.isReady() && batch.getCollectionPointId() != null) {
                maxHeap.add(batch);
            }
        }

        if (maxHeap.isEmpty()) {
            return DispatchResponseDto.builder()
                    .statusMessage("No ready harvest batches available for dispatch")
                    .totalRouteCost(-1.0)
                    .recommendedPath(Collections.emptyList())
                    .build();
        }

        // 2 & 3. Iterative Selection & Accessibility Fallback
        while (!maxHeap.isEmpty()) {
            HarvestBatch topCandidate = maxHeap.poll();
            String targetCollectionPoint = topCandidate.getCollectionPointId();

            DijkstraService.PathResult pathResult = dijkstraService.findShortestPath(
                    truckStartNode,
                    targetCollectionPoint,
                    roadGraph
            );

            if (pathResult.isReachable()) {
                return DispatchResponseDto.builder()
                        .selectedBatchId(topCandidate.getBatchId())
                        .targetCollectionPoint(targetCollectionPoint)
                        .priorityScore(topCandidate.getPriorityScore())
                        .recommendedPath(pathResult.getPath())
                        .totalRouteCost(pathResult.getTotalCost())
                        .statusMessage("Optimal route successfully computed to highest priority accessible target")
                        .build();
            }
        }

        // 4. Exhausted candidates with no accessible route
        return DispatchResponseDto.builder()
                .statusMessage("No accessible collection targets found in the road network")
                .totalRouteCost(-1.0)
                .recommendedPath(Collections.emptyList())
                .build();
    }
}
