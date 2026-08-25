package com.agripulse.spoilage_risk_ranking.datastructure;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Member 8 core data structure.
 *
 * A custom max-heap keeps the highest-risk harvest batch at index 0.
 * The companion id -> index map lets a persisted batch be updated in
 * O(log n) after its risk score changes.
 *
 * Complexity:
 *  - buildHeap: O(n)
 *  - insert: O(log n)
 *  - peekMax: O(1)
 *  - extractMax: O(log n)
 *  - upsert/update existing persisted batch: O(log n)
 *  - space: O(n)
 */
public class HarvestBatchMaxHeap {

    private final List<HarvestBatch> heap = new ArrayList<>();
    private final Map<Long, Integer> indexById = new HashMap<>();

    public void clear() {
        heap.clear();
        indexById.clear();
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /** Build a heap in O(n) using bottom-up heapify. */
    public void buildHeap(List<HarvestBatch> batches) {
        clear();
        heap.addAll(batches);
        rebuildIndexMap();
        for (int i = parentIndex(heap.size() - 1); i >= 0; i--) {
            siftDown(i);
        }
    }

    /** Insert a new incoming batch in O(log n). */
    public void insert(HarvestBatch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("batch cannot be null");
        }
        if (batch.getRiskScore() == null) {
            throw new IllegalArgumentException("riskScore must be calculated before inserting into the max-heap");
        }

        Long id = batch.getId();
        if (id != null && indexById.containsKey(id)) {
            upsert(batch);
            return;
        }

        heap.add(batch);
        int index = heap.size() - 1;
        putIndex(batch, index);
        siftUp(index);
    }

    /**
     * Insert a batch or replace an existing persisted batch with the same id.
     * Reheapification in either direction is O(log n).
     */
    public void upsert(HarvestBatch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("batch cannot be null");
        }
        if (batch.getRiskScore() == null) {
            throw new IllegalArgumentException("riskScore must be calculated before updating the max-heap");
        }

        Long id = batch.getId();
        if (id == null || !indexById.containsKey(id)) {
            insert(batch);
            return;
        }

        int index = indexById.get(id);
        heap.set(index, batch);
        putIndex(batch, index);

        int parent = parentIndex(index);
        if (index > 0 && higherPriority(heap.get(index), heap.get(parent))) {
            siftUp(index);
        } else {
            siftDown(index);
        }
    }

    /** Return the current highest-risk batch without removing it. O(1). */
    public HarvestBatch peekMax() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return heap.get(0);
    }

    /** Remove and return the current highest-risk batch. O(log n). */
    public HarvestBatch extractMax() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }

        HarvestBatch max = heap.get(0);
        removeIndex(max);

        if (heap.size() == 1) {
            heap.remove(0);
            return max;
        }

        HarvestBatch last = heap.remove(heap.size() - 1);
        heap.set(0, last);
        putIndex(last, 0);
        siftDown(0);
        return max;
    }

    /** Returns a copy in internal heap-array order; root is always the maximum. */
    public List<HarvestBatch> snapshotHeapOrder() {
        return new ArrayList<>(heap);
    }

    /**
     * Returns a fully ordered copy (highest risk first) without changing the
     * live heap. This is for API display/debugging, not for normal top retrieval.
     */
    public List<HarvestBatch> snapshotPriorityOrder() {
        HarvestBatchMaxHeap copy = new HarvestBatchMaxHeap();
        copy.buildHeap(heap);
        List<HarvestBatch> ordered = new ArrayList<>();
        while (!copy.isEmpty()) {
            ordered.add(copy.extractMax());
        }
        return ordered;
    }

    private void siftUp(int index) {
        int current = index;
        while (current > 0) {
            int parent = parentIndex(current);
            if (!higherPriority(heap.get(current), heap.get(parent))) {
                break;
            }
            swap(current, parent);
            current = parent;
        }
    }

    private void siftDown(int index) {
        int current = index;
        while (true) {
            int left = leftChild(current);
            int right = rightChild(current);
            int largest = current;

            if (left < heap.size() && higherPriority(heap.get(left), heap.get(largest))) {
                largest = left;
            }
            if (right < heap.size() && higherPriority(heap.get(right), heap.get(largest))) {
                largest = right;
            }
            if (largest == current) {
                return;
            }

            swap(current, largest);
            current = largest;
        }
    }

    /**
     * Higher risk score wins. Ties are deterministic: older harvest first,
     * then smaller database id first.
     */
    private boolean higherPriority(HarvestBatch a, HarvestBatch b) {
        double aRisk = a.getRiskScore() == null ? Double.NEGATIVE_INFINITY : a.getRiskScore();
        double bRisk = b.getRiskScore() == null ? Double.NEGATIVE_INFINITY : b.getRiskScore();

        int riskCompare = Double.compare(aRisk, bRisk);
        if (riskCompare != 0) {
            return riskCompare > 0;
        }

        LocalDateTime aTime = a.getHarvestTime();
        LocalDateTime bTime = b.getHarvestTime();
        if (aTime != null && bTime != null && !aTime.equals(bTime)) {
            return aTime.isBefore(bTime); // older waiting batch gets priority
        }

        if (a.getId() != null && b.getId() != null && !a.getId().equals(b.getId())) {
            return a.getId() < b.getId();
        }
        return false;
    }

    private void swap(int a, int b) {
        HarvestBatch first = heap.get(a);
        HarvestBatch second = heap.get(b);
        heap.set(a, second);
        heap.set(b, first);
        putIndex(second, a);
        putIndex(first, b);
    }

    private void rebuildIndexMap() {
        indexById.clear();
        for (int i = 0; i < heap.size(); i++) {
            putIndex(heap.get(i), i);
        }
    }

    private void putIndex(HarvestBatch batch, int index) {
        if (batch.getId() != null) {
            indexById.put(batch.getId(), index);
        }
    }

    private void removeIndex(HarvestBatch batch) {
        if (batch.getId() != null) {
            indexById.remove(batch.getId());
        }
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftChild(int index) {
        return (2 * index) + 1;
    }

    private int rightChild(int index) {
        return (2 * index) + 2;
    }
}
