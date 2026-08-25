package com.agripulse.spoilage_risk_ranking.datastructure;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HarvestBatchMaxHeapTest {

    @Test
    void highestRiskBatchStaysAtRoot() {
        HarvestBatchMaxHeap heap = new HarvestBatchMaxHeap();
        heap.insert(batch(1L, 10.0));
        heap.insert(batch(2L, 25.0));
        heap.insert(batch(3L, 15.0));

        assertEquals(2L, heap.peekMax().getId());
        assertEquals(25.0, heap.peekMax().getRiskScore());
    }

    @Test
    void extractMaxReturnsDescendingRiskOrder() {
        HarvestBatchMaxHeap heap = new HarvestBatchMaxHeap();
        heap.buildHeap(List.of(
                batch(1L, 10.0),
                batch(2L, 30.0),
                batch(3L, 20.0)
        ));

        assertEquals(30.0, heap.extractMax().getRiskScore());
        assertEquals(20.0, heap.extractMax().getRiskScore());
        assertEquals(10.0, heap.extractMax().getRiskScore());
    }

    @Test
    void upsertRepositionsChangedRiskInLogarithmicHeapOperations() {
        HarvestBatchMaxHeap heap = new HarvestBatchMaxHeap();
        HarvestBatch first = batch(1L, 10.0);
        HarvestBatch second = batch(2L, 20.0);
        heap.buildHeap(List.of(first, second));

        first.setRiskScore(40.0);
        heap.upsert(first);

        assertEquals(1L, heap.peekMax().getId());
        assertEquals(40.0, heap.peekMax().getRiskScore());
    }

    private HarvestBatch batch(Long id, double risk) {
        HarvestBatch batch = new HarvestBatch();
        batch.setId(id);
        batch.setRiskScore(risk);
        batch.setHarvestTime(LocalDateTime.now().minusHours(id));
        batch.setStatus("ready");
        return batch;
    }
}
