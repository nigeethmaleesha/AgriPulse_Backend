package com.agripulse.spoilage_risk_ranking.service;

import com.agripulse.spoilage_risk_ranking.datastructure.HarvestBatchMaxHeap;
import com.agripulse.spoilage_risk_ranking.dto.PriorityQueueStatusResponse;
import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import com.agripulse.spoilage_risk_ranking.repository.HarvestBatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Member 8 scope: live highest-risk retrieval using a custom max-heap.
 *
 * Integration with Member 7:
 *  - both members use the SAME HarvestBatch entity/table
 *  - Member 7 owns the risk-score formula
 *  - Member 8 reuses RiskRankingService to calculate/refresh riskScore
 *    before a batch enters the heap
 *
 * No separate priority_queue database table is needed: PostgreSQL is the
 * source of truth, while the max-heap is the live in-memory data structure.
 */
@Service
public class PriorityQueueService {

    private final HarvestBatchRepository batchRepository;
    private final RiskRankingService riskRankingService;
    private final HarvestBatchMaxHeap liveHeap = new HarvestBatchMaxHeap();
    private boolean initialized = false;

    public PriorityQueueService(HarvestBatchRepository batchRepository,
                                RiskRankingService riskRankingService) {
        this.batchRepository = batchRepository;
        this.riskRankingService = riskRankingService;
    }

    /** Reload every ready batch from the shared Member 7/8 table and rebuild the heap. */
    public synchronized PriorityQueueStatusResponse reloadFromDatabase() {
        List<HarvestBatch> ready = batchRepository.findByStatus("ready");
        riskRankingService.scoreAll(ready); // reuse Member 7's single risk formula
        batchRepository.saveAll(ready);     // keep shared risk_score values synchronized
        liveHeap.buildHeap(ready);
        initialized = true;
        return status();
    }

    /** Persist a newly arriving batch, calculate its score, then insert it in O(log n). */
    public synchronized HarvestBatch createAndEnqueue(HarvestBatch batch) {
        batch.setId(null);
        batch.setStatus("ready");
        batch.setRiskScore(riskRankingService.calculateRiskScore(batch));
        HarvestBatch saved = batchRepository.save(batch);
        liveHeap.insert(saved);
        initialized = true;
        return saved;
    }

    /** Load an already persisted ready batch and insert/update it in the live heap. */
    public synchronized HarvestBatch enqueueExisting(Long batchId) {
        HarvestBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new NoSuchElementException("Harvest batch not found: " + batchId));
        ensureReady(batch);
        refreshScore(batch);
        liveHeap.upsert(batch);
        initialized = true;
        return batch;
    }

    /** Recalculate Member 7's score and update the heap position in O(log n). */
    public synchronized HarvestBatch refreshPriority(Long batchId) {
        HarvestBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new NoSuchElementException("Harvest batch not found: " + batchId));
        ensureReady(batch);
        refreshScore(batch);
        liveHeap.upsert(batch);
        initialized = true;
        return batch;
    }

    /** O(1) top-risk lookup after the heap exists. */
    public synchronized HarvestBatch peekHighestRisk() {
        ensureLoaded();
        return liveHeap.peekMax();
    }

    /** O(log n) remove-from-live-queue operation. Does not alter database status. */
    public synchronized HarvestBatch popHighestRisk() {
        ensureLoaded();
        return liveHeap.extractMax();
    }

    public synchronized List<HarvestBatch> heapOrder() {
        ensureLoaded();
        return liveHeap.snapshotHeapOrder();
    }

    public synchronized List<HarvestBatch> priorityOrder() {
        ensureLoaded();
        return liveHeap.snapshotPriorityOrder();
    }

    public synchronized PriorityQueueStatusResponse status() {
        HarvestBatch top = liveHeap.isEmpty() ? null : liveHeap.peekMax();
        return new PriorityQueueStatusResponse(liveHeap.size(), liveHeap.isEmpty(), top);
    }

    public synchronized PriorityQueueStatusResponse clear() {
        liveHeap.clear();
        initialized = true;
        return status();
    }

    private void refreshScore(HarvestBatch batch) {
        batch.setRiskScore(riskRankingService.calculateRiskScore(batch));
        batchRepository.save(batch);
    }

    private void ensureReady(HarvestBatch batch) {
        if (!"ready".equalsIgnoreCase(batch.getStatus())) {
            throw new IllegalArgumentException("Only batches with status 'ready' can enter the live priority queue");
        }
    }

    private void ensureLoaded() {
        if (!initialized) {
            reloadFromDatabase();
        }
        if (liveHeap.isEmpty()) {
            throw new NoSuchElementException("No ready harvest batches are available");
        }
    }
}
