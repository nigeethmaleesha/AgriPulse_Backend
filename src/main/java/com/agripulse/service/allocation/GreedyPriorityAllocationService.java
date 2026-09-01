package com.agripulse.service.allocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.FertilizerRequest;

/**
 * Greedy priority allocation baseline — takes whole requests in order of
 * highest benefitScore first, skipping any that don't fit, until capacity
 * runs out. No splitting, no optimality guarantee. Third comparison
 * candidate per the AgriPulse blueprint (Member 3 candidate algorithms).
 */
@Service
public class GreedyPriorityAllocationService {

    public AllocationResultDto allocate(List<FertilizerRequest> requests, int totalCapacity) {

        if (requests == null || requests.isEmpty()) {
            throw new FertilizerAllocationException("No fertilizer requests available for allocation.");
        }
        if (totalCapacity < 0) {
            throw new FertilizerAllocationException("Total capacity cannot be negative.");
        }

        long startTime = System.currentTimeMillis();

        List<FertilizerRequest> sorted = new ArrayList<>(requests);

        // Sort descending by benefitScore alone (pure priority, not ratio-based)
        sorted.sort(Comparator.comparingDouble(FertilizerRequest::getBenefitScore).reversed());

        List<FertilizerRequest> allocated = new ArrayList<>();
        List<FertilizerRequest> rejected = new ArrayList<>();

        int remainingCapacity = totalCapacity;
        double totalBenefitAchieved = 0.0;
        int totalRequestedBags = 0;

        for (FertilizerRequest r : requests) {
            totalRequestedBags += r.getRequestedBags();
        }

        for (FertilizerRequest request : sorted) {
            if (request.getRequestedBags() <= remainingCapacity) {
                request.setStatus("ALLOCATED");
                request.setAllocatedBags(request.getRequestedBags());
                allocated.add(request);

                totalBenefitAchieved += request.getBenefitScore();
                remainingCapacity -= request.getRequestedBags();
            } else {
                // Doesn't fit in whatever capacity remains — skip it (no splitting)
                request.setStatus("REJECTED");
                request.setAllocatedBags(0);
                rejected.add(request);
            }
        }

        int usedCapacity = totalCapacity - remainingCapacity;

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        return new AllocationResultDto(
                allocated,
                rejected,
                totalCapacity,
                usedCapacity,
                totalRequestedBags,
                totalBenefitAchieved,
                executionTime
        );
    }
}