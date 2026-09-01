package com.agripulse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agripulse.dto.AlgorithmComparisonDto;
import com.agripulse.dto.AllocationRequestDto;
import com.agripulse.dto.AllocationResultDto;
import com.agripulse.dto.FertilizerRequestCreateDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.Farm;
import com.agripulse.model.FertilizerRequest;
import com.agripulse.repository.FarmRepository;
import com.agripulse.repository.FertilizerRequestRepository;
import com.agripulse.service.allocation.BenchmarkService;
import com.agripulse.service.allocation.FertilizerAllocationService;
import com.agripulse.service.allocation.FractionalKnapsackService;
import com.agripulse.service.allocation.GreedyPriorityAllocationService;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/fertilizer")
public class FertilizerController {

    private final FertilizerRequestRepository repository;
    private final FarmRepository farmRepository;
    private final FertilizerAllocationService allocationService;
    private final FractionalKnapsackService fractionalService;
    private final GreedyPriorityAllocationService greedyService;
    private final BenchmarkService benchmarkService;

    @Autowired
    public FertilizerController(FertilizerRequestRepository repository,
                                 FarmRepository farmRepository,
                                 FertilizerAllocationService allocationService,
                                 FractionalKnapsackService fractionalService,
                                 GreedyPriorityAllocationService greedyService,
                                 BenchmarkService benchmarkService) {
        this.repository = repository;
        this.farmRepository = farmRepository;
        this.allocationService = allocationService;
        this.fractionalService = fractionalService;
        this.greedyService = greedyService;
        this.benchmarkService = benchmarkService;
    }

    // Create a fertilizer request — now requires an existing farmId
@PostMapping("/requests")
public ResponseEntity<FertilizerRequest> createRequest(@RequestBody FertilizerRequestCreateDto dto) {

    if (dto.getContactNumber() == null || dto.getContactNumber().isBlank()) {
        throw new FertilizerAllocationException("Farm contact number is required.");
    }
    if (dto.getFarmName() == null || dto.getFarmName().isBlank()) {
        throw new FertilizerAllocationException("Farm name is required.");
    }

    Farm farm = farmRepository.findByContactNumber(dto.getContactNumber())
            .map(existingFarm -> {
                // Farm already registered — auto-fill region/cropType/landSize from existing record.
                // Verify the submitted farm name matches, to catch typos/mixups.
                if (!dto.getFarmName().equalsIgnoreCase(existingFarm.getFarmName())) {
                    throw new FertilizerAllocationException(
                            "Contact number " + dto.getContactNumber() + " is already registered to farm '"
                                    + existingFarm.getFarmName() + "', not '" + dto.getFarmName() + "'. "
                                    + "Please verify the details."
                    );
                }
                return existingFarm;
            })
            .orElseGet(() -> {
                // New farm — region is the minimum required info to register it
                if (dto.getRegion() == null || dto.getRegion().isBlank()) {
                    throw new FertilizerAllocationException(
                            "This is a new farm (contact number not found). Region is required to register it."
                    );
                }
                Farm newFarm = new Farm(
                        dto.getFarmName(),
                        dto.getContactNumber(),
                        dto.getRegion(),
                        dto.getCropType(),
                        dto.getLandSize()
                );
                return farmRepository.save(newFarm);
            });

    FertilizerRequest request = new FertilizerRequest(
            farm,
            dto.getFertilizerType(),
            dto.getRequestedBags(),
            dto.getBenefitScore(),
            dto.getUrgencyLevel()
    );

    FertilizerRequest saved = repository.save(request);
    return ResponseEntity.ok(saved);
}

    @GetMapping("/requests")
    public ResponseEntity<List<FertilizerRequest>> getAllRequests() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<FertilizerRequest>> getPendingRequests() {
        return ResponseEntity.ok(repository.findByStatus("PENDING"));
    }

    // Production allocation — 0/1 Knapsack DP (persists results to DB)
    @PostMapping("/allocate")
    public ResponseEntity<AllocationResultDto> allocate(@RequestBody AllocationRequestDto requestDto) {
        List<FertilizerRequest> pending = repository.findByStatus("PENDING");
        AllocationResultDto result = allocationService.allocate(pending, requestDto.getTotalCapacity());

        repository.saveAll(result.getAllocatedRequests());
        repository.saveAll(result.getRejectedRequests());

        return ResponseEntity.ok(result);
    }

    // Comparison baseline — Fractional Knapsack (does NOT persist; evaluation only)
    @PostMapping("/allocate/fractional")
    public ResponseEntity<AllocationResultDto> allocateFractional(@RequestBody AllocationRequestDto requestDto) {
        List<FertilizerRequest> pending = repository.findByStatus("PENDING");
        AllocationResultDto result = fractionalService.allocate(pending, requestDto.getTotalCapacity());
        return ResponseEntity.ok(result);
    }

    // Comparison baseline — Greedy Priority Allocation (does NOT persist; evaluation only)
    @PostMapping("/allocate/greedy")
    public ResponseEntity<AllocationResultDto> allocateGreedy(@RequestBody AllocationRequestDto requestDto) {
        List<FertilizerRequest> pending = repository.findByStatus("PENDING");
        AllocationResultDto result = greedyService.allocate(pending, requestDto.getTotalCapacity());
        return ResponseEntity.ok(result);
    }

    // Full benchmark — runs all 3 algorithms at 20/200/2000 synthetic requests
    @GetMapping("/benchmark")
    public ResponseEntity<List<AlgorithmComparisonDto>> runBenchmark() {
        List<AlgorithmComparisonDto> results = benchmarkService.runFullBenchmark();
        return ResponseEntity.ok(results);
    }
}