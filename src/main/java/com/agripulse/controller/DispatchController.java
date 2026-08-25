package com.agripulse.controller;

import com.agripulse.dto.DispatchRequestDto;
import com.agripulse.dto.DispatchResponseDto;
import com.agripulse.dto.RoadStatusUpdateDto;
import com.agripulse.model.entity.HarvestBatchEntity;
import com.agripulse.model.entity.Road;
import com.agripulse.service.routing.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dispatch")
@CrossOrigin(origins = "*")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    /**
     * POST /api/v1/dispatch/calculate-route
     * In-memory route calculation using request payload.
     */
    @PostMapping("/calculate-route")
    public ResponseEntity<DispatchResponseDto> calculateRoute(@Valid @RequestBody DispatchRequestDto request) {
        DispatchResponseDto response = dispatchService.calculateDispatchRoute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/dispatch/next-route?truckNode=C1
     * Calculates optimal route to highest priority ready batch using live database roads & batches.
     */
    @GetMapping("/next-route")
    public ResponseEntity<DispatchResponseDto> getNextRoute(@RequestParam(defaultValue = "C1") String truckNode) {
        DispatchResponseDto response = dispatchService.calculateNextRouteFromDatabase(truckNode);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/dispatch/roads/{id}/status
     * Dynamic update for road closure or monsoon status in database.
     */
    @PutMapping("/roads/{id}/status")
    public ResponseEntity<Road> updateRoadStatus(@PathVariable Long id, @RequestBody RoadStatusUpdateDto updateDto) {
        Road updatedRoad = dispatchService.updateRoadStatus(id, updateDto);
        return ResponseEntity.ok(updatedRoad);
    }

    /**
     * PUT /api/v1/dispatch/batches/{id}/collect
     * Marks batch as "COLLECTED" in database and returns recalculated route from truckNode.
     */
    @PutMapping("/batches/{id}/collect")
    public ResponseEntity<DispatchResponseDto> markBatchCollected(@PathVariable String id,
                                                                   @RequestParam(defaultValue = "C1") String truckNode) {
        HarvestBatchEntity collectedBatch = dispatchService.markBatchAsCollected(id);
        DispatchResponseDto nextRouteResponse = dispatchService.calculateNextRouteFromDatabase(truckNode);
        nextRouteResponse.setStatusMessage("Batch " + collectedBatch.getId() + " marked as COLLECTED. " + nextRouteResponse.getStatusMessage());
        return ResponseEntity.ok(nextRouteResponse);
    }

    /**
     * POST /api/v1/dispatch/seed-data
     * Seeds initial collection points (C1-C6), roads, and harvest batches (B-102, B-091) into database.
     */
    @PostMapping("/seed-data")
    public ResponseEntity<String> seedData() {
        String resultMessage = dispatchService.seedInitialData();
        return ResponseEntity.ok(resultMessage);
    }
}
