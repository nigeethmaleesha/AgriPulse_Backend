package com.agripulse.service.allocation;

import com.agripulse.model.PumpRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class PumpAllocationService {


    // ALGORITHM 1: MAX-HEAP ( Primary Production Algorithm)

    public List<PumpRequest> allocatePumpsWithMaxHeap(List<PumpRequest> allRequests, int availablePumps) {
        PriorityQueue<PumpRequest> maxHeap = new PriorityQueue<>(
                (request1, request2) -> Double.compare(request2.getPriorityScore(), request1.getPriorityScore())
        );

        for (PumpRequest request : allRequests) {
            if (request.isEligible()) {
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


    // ALGORITHM 2: TIMSORT / MERGE SORT

    public List<PumpRequest> allocatePumpsWithSort(List<PumpRequest> allRequests, int availablePumps) {
        List<PumpRequest> eligibleFarms = new ArrayList<>();
        for (PumpRequest request : allRequests) {
            if (request.isEligible()) {
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


    // ALGORITHM 3: SIMPLE GREEDY

    public List<PumpRequest> allocatePumpsGreedyBaseline(List<PumpRequest> allRequests, int availablePumps) {
        List<PumpRequest> allocatedFarms = new ArrayList<>();

        for (PumpRequest request : allRequests) {
            if (availablePumps == 0) break;

            if (request.isEligible()) {
                allocatedFarms.add(request);
                availablePumps--;
            }
        }
        return allocatedFarms;
    }
}