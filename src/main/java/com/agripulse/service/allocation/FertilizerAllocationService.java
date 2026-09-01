package com.agripulse.service.allocation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agripulse.dto.AllocationResultDto;
import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.FertilizerRequest;

@Service
public class FertilizerAllocationService {

    public AllocationResultDto allocate(List<FertilizerRequest> requests, int totalCapacity) {

        if (requests == null || requests.isEmpty()) {
            throw new FertilizerAllocationException("No fertilizer requests available for allocation.");
        }
        if (totalCapacity < 0) {
            throw new FertilizerAllocationException("Total capacity cannot be negative.");
        }

        long startTime = System.currentTimeMillis();

        int n = requests.size();

        double[][] dp = new double[n + 1][totalCapacity + 1];
        boolean[][] keep = new boolean[n + 1][totalCapacity + 1];

        for (int i = 1; i <= n; i++) {
            FertilizerRequest current = requests.get(i - 1);
            int weight = current.getRequestedBags();
            double value = current.getBenefitScore();

            for (int w = 0; w <= totalCapacity; w++) {
                if (weight > w || weight <= 0) {
                    dp[i][w] = dp[i - 1][w];
                } else {
                    double excludeValue = dp[i - 1][w];
                    double includeValue = dp[i - 1][w - weight] + value;
                    if (includeValue > excludeValue + 1e-9) {
                        dp[i][w] = includeValue;
                        keep[i][w] = true;
                    } else {
                        dp[i][w] = excludeValue;
                        keep[i][w] = false;
                    }
                }
            }
        }

        List<FertilizerRequest> allocated = new ArrayList<>();
        List<FertilizerRequest> rejected = new ArrayList<>();

        int remainingCapacity = totalCapacity;
        int totalRequestedBags = 0;

        for (FertilizerRequest r : requests) {
            totalRequestedBags += r.getRequestedBags();
        }

        for (int i = n; i >= 1; i--) {
            FertilizerRequest req = requests.get(i - 1);
            if (keep[i][remainingCapacity]) {
                req.setStatus("ALLOCATED");
                req.setAllocatedBags(req.getRequestedBags());
                allocated.add(req);
                remainingCapacity -= req.getRequestedBags();
            } else {
                req.setStatus("REJECTED");
                req.setAllocatedBags(0);
                rejected.add(req);
            }
        }

        double totalBenefitAchieved = dp[n][totalCapacity];
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