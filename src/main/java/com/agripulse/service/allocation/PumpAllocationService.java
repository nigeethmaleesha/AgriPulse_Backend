package com.agripulse.service.allocation;

import com.agripulse.model.PumpRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class PumpAllocationService {

    // ALGORITHM 1: MAX-HEAP (Primary Production Algorithm) - UNCHANGED, was already correct
    public List<PumpRequest> allocatePumpsWithMaxHeap(List<PumpRequest> allRequests, int availablePumps) {
        if (allRequests == null || availablePumps <= 0) {
            return Collections.emptyList();
        }

        PriorityQueue<PumpRequest> maxHeap = new PriorityQueue<>(
                (request1, request2) -> Double.compare(request2.getPriorityScore(), request1.getPriorityScore())
        );

        for (PumpRequest request : allRequests) {
            if (request != null && request.isEligible()) {
                maxHeap.add(request);
            }
        }

        List<PumpRequest> allocatedFarms = new ArrayList<>();
        while (availablePumps > 0 && !maxHeap.isEmpty()) {
            allocatedFarms.add(maxHeap.poll());
            availablePumps--;
        }
        return allocatedFarms;
    }

    // ALGORITHM 2: TIMSORT / MERGE SORT - UNCHANGED, was already correct
    public List<PumpRequest> allocatePumpsWithSort(List<PumpRequest> allRequests, int availablePumps) {
        if (allRequests == null || availablePumps <= 0) {
            return Collections.emptyList();
        }

        List<PumpRequest> eligibleFarms = new ArrayList<>();
        for (PumpRequest request : allRequests) {
            if (request != null && request.isEligible()) {
                eligibleFarms.add(request);
            }
        }

        eligibleFarms.sort((req1, req2) -> Double.compare(req2.getPriorityScore(), req1.getPriorityScore()));

        List<PumpRequest> allocatedFarms = new ArrayList<>();
        for (int i = 0; i < Math.min(availablePumps, eligibleFarms.size()); i++) {
            allocatedFarms.add(eligibleFarms.get(i));
        }
        return allocatedFarms;
    }

    // ALGORITHM 3: SIMPLE GREEDY BASELINE - FIXED
    //
    // BUG THAT WAS HERE: the original version walked allRequests in whatever
    // order the list arrived in and took the first N eligible farms - it never
    // looked at priorityScore at all. That is not a "greedy" algorithm on this
    // problem; it's arbitrary/list-order selection, and it broke the
    // correctness invariant that all three strategies should reach the same
    // total benefit on the same input.
    //
    // FIX: repeatedly scan the remaining eligible farms for the current
    // highest priorityScore, same textbook definition as "simple greedy" used
    // as a baseline elsewhere in this project (e.g. Linear-Scan comparisons).
    // This intentionally does NOT sort or use a heap - it stays O(k * n),
    // which is the whole point of keeping it as a weak baseline for the
    // benchmark comparison table.
    public List<PumpRequest> allocatePumpsGreedyBaseline(List<PumpRequest> allRequests, int availablePumps) {
        if (allRequests == null || availablePumps <= 0) {
            return Collections.emptyList();
        }

        List<PumpRequest> eligibleFarms = new ArrayList<>();
        for (PumpRequest request : allRequests) {
            if (request != null && request.isEligible()) {
                eligibleFarms.add(request);
            }
        }

        List<PumpRequest> allocatedFarms = new ArrayList<>();

        while (availablePumps > 0 && !eligibleFarms.isEmpty()) {
            int bestIndex = 0;
            double bestScore = eligibleFarms.get(0).getPriorityScore();

            for (int i = 1; i < eligibleFarms.size(); i++) {
                double score = eligibleFarms.get(i).getPriorityScore();
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = i;
                }
            }

            allocatedFarms.add(eligibleFarms.remove(bestIndex));
            availablePumps--;
        }

        return allocatedFarms;
    }
}