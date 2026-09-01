package com.agripulse.service.allocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.FertilizerRequest;

/**
 * Fractional Knapsack (greedy) baseline for comparison against 0/1 Knapsack DP.
 * Allows partial allocation of a request's bags — NOT used as the production
 * allocation method, since real fertilizer requests represent a farm's whole
 * seasonal need. Implemented purely as an academic comparison baseline
 * (see AgriPulse blueprint, Member 3 candidate algorithms).
 */
@Service
public class FractionalKnapsackService {

    public AllocationResultDto allocate(List<FertilizerRequest> requests, int totalCapacity) {

        if (requests == null || requests.isEmpty()) {
            throw new FertilizerAllocationException("No fertilizer requests available for allocation.");
        }
        if (totalCapacity < 0) {
            throw new FertilizerAllocationException("Total capacity cannot be negative.");
        }

        long startTime = System.currentTimeMillis();

        // Work on a copy so we don't disturb the original list ordering
        List<FertilizerRequest> sorted = new ArrayList<>(requests);

        // Sort descending by value/weight ratio (benefit per bag)
        sorted.sort(Comparator.comparingDouble(
                (FertilizerRequest r) -> r.getBenefitScore() / r.getRequestedBags()
        ).reversed());

        List<FertilizerRequest> allocated = new ArrayList<>();
        List<FertilizerRequest> rejected = new ArrayList<>();

        int remainingCapacity = totalCapacity;
        double totalBenefitAchieved = 0.0;
        int totalRequestedBags = 0;

        for (FertilizerRequest r : requests) {
            totalRequestedBags += r.getRequestedBags();
        }

        for (FertilizerRequest request : sorted) {
            if (remainingCapacity <= 0) {
                request.setStatus("REJECTED");
                request.setAllocatedBags(0);
                rejected.add(request);
                continue;
            }

            if (request.getRequestedBags() <= remainingCapacity) {
                // Full allocation — fits entirely
                request.setStatus("ALLOCATED");
                request.setAllocatedBags(request.getRequestedBags());
                allocated.add(request);

                totalBenefitAchieved += request.getBenefitScore();
                remainingCapacity -= request.getRequestedBags();

            } else {
                // Partial allocation — take a fraction to fill remaining capacity exactly
                double fraction = (double) remainingCapacity / request.getRequestedBags();
                double partialBenefit = request.getBenefitScore() * fraction;

                request.setStatus("ALLOCATED"); // partially fulfilled, still counts as allocated
                request.setAllocatedBags(remainingCapacity); // whatever capacity is left
                allocated.add(request);

                totalBenefitAchieved += partialBenefit;
                remainingCapacity = 0;
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