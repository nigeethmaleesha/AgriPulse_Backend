package com.agripulse.spoilage_risk_ranking.service;

import com.agripulse.spoilage_risk_ranking.datastructure.HarvestBatchMaxHeap;
import com.agripulse.spoilage_risk_ranking.dto.PriorityBenchmarkResponse;
import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;
import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import com.agripulse.spoilage_risk_ranking.repository.AlgorithmTestResultRepository;
import com.agripulse.spoilage_risk_ranking.util.SyntheticBatchGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Member 8 experimental comparison.
 *
 * The three blueprint candidates are tested as LIVE insertion strategies:
 *  1) custom Max-Heap / Priority Queue
 *  2) Java object-list sort (TimSort) after every incoming batch
 *  3) insertion-ordered list (Insertion Sort style incremental placement)
 *
 * The same base data and same incoming stream are used for each algorithm.
 */
@Service
public class PriorityQueueBenchmarkService {

    private static final String MODULE = "spoilage_priority_queue";
    private static final int DEFAULT_INCOMING_OPERATIONS = 50;

    private final RiskRankingService riskRankingService;
    private final AlgorithmTestResultRepository resultRepository;

    private final Comparator<HarvestBatch> descendingRisk = Comparator
            .comparing(HarvestBatch::getRiskScore, Comparator.nullsLast(Double::compareTo))
            .reversed();

    public PriorityQueueBenchmarkService(RiskRankingService riskRankingService,
                                         AlgorithmTestResultRepository resultRepository) {
        this.riskRankingService = riskRankingService;
        this.resultRepository = resultRepository;
    }

    public PriorityBenchmarkResponse run(int[] sizes, Integer incomingOperations) {
        int operations = incomingOperations == null || incomingOperations <= 0
                ? DEFAULT_INCOMING_OPERATIONS
                : incomingOperations;

        List<AlgorithmTestResult> currentRun = new ArrayList<>();
        for (int size : sizes) {
            if (size <= 0) {
                throw new IllegalArgumentException("Benchmark sizes must be positive");
            }

            List<HarvestBatch> base = SyntheticBatchGenerator.generate(size, 4200L + size);
            List<HarvestBatch> incoming = SyntheticBatchGenerator.generate(operations, 8400L + size);
            riskRankingService.scoreAll(base);
            riskRankingService.scoreAll(incoming);

            currentRun.add(runMaxHeap(size, base, incoming));
            currentRun.add(runTimSortResort(size, base, incoming));
            currentRun.add(runInsertionOrder(size, base, incoming));
        }

        resultRepository.saveAll(currentRun);
        return new PriorityBenchmarkResponse(
                currentRun,
                "executionTimeMs measures processing of the incoming stream; solutionMetric stores average top-risk retrieval time and top score"
        );
    }

    public List<AlgorithmTestResult> savedResults() {
        return resultRepository.findByModule(MODULE);
    }

    private AlgorithmTestResult runMaxHeap(int inputSize,
                                           List<HarvestBatch> base,
                                           List<HarvestBatch> incoming) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = usedMemory(runtime);

        HarvestBatchMaxHeap heap = new HarvestBatchMaxHeap();
        heap.buildHeap(new ArrayList<>(base));

        long topNanos = 0;
        long start = System.nanoTime();
        for (HarvestBatch batch : incoming) {
            heap.insert(batch);
            long peekStart = System.nanoTime();
            heap.peekMax();
            topNanos += System.nanoTime() - peekStart;
        }
        long elapsed = System.nanoTime() - start;
        long memoryAfter = usedMemory(runtime);

        return result(
                "max_heap",
                inputSize,
                elapsed,
                memoryBefore,
                memoryAfter,
                average(topNanos, incoming.size()),
                heap.peekMax().getRiskScore()
        );
    }

    private AlgorithmTestResult runTimSortResort(int inputSize,
                                                  List<HarvestBatch> base,
                                                  List<HarvestBatch> incoming) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = usedMemory(runtime);

        List<HarvestBatch> list = new ArrayList<>(base);
        list.sort(descendingRisk);

        long topNanos = 0;
        long start = System.nanoTime();
        for (HarvestBatch batch : incoming) {
            list.add(batch);
            list.sort(descendingRisk); // full re-sort after every arrival
            long peekStart = System.nanoTime();
            list.get(0);
            topNanos += System.nanoTime() - peekStart;
        }
        long elapsed = System.nanoTime() - start;
        long memoryAfter = usedMemory(runtime);

        return result(
                "timsort_full_resort",
                inputSize,
                elapsed,
                memoryBefore,
                memoryAfter,
                average(topNanos, incoming.size()),
                list.get(0).getRiskScore()
        );
    }

    private AlgorithmTestResult runInsertionOrder(int inputSize,
                                                   List<HarvestBatch> base,
                                                   List<HarvestBatch> incoming) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryBefore = usedMemory(runtime);

        List<HarvestBatch> ordered = new ArrayList<>(base);
        ordered.sort(descendingRisk);

        long topNanos = 0;
        long start = System.nanoTime();
        for (HarvestBatch batch : incoming) {
            insertDescending(ordered, batch);
            long peekStart = System.nanoTime();
            ordered.get(0);
            topNanos += System.nanoTime() - peekStart;
        }
        long elapsed = System.nanoTime() - start;
        long memoryAfter = usedMemory(runtime);

        return result(
                "insertion_order",
                inputSize,
                elapsed,
                memoryBefore,
                memoryAfter,
                average(topNanos, incoming.size()),
                ordered.get(0).getRiskScore()
        );
    }

    /** Insertion-Sort style placement: scan until the correct descending slot. */
    private void insertDescending(List<HarvestBatch> ordered, HarvestBatch batch) {
        int index = 0;
        while (index < ordered.size()
                && ordered.get(index).getRiskScore() >= batch.getRiskScore()) {
            index++;
        }
        ordered.add(index, batch);
    }

    private AlgorithmTestResult result(String algorithm,
                                       int inputSize,
                                       long elapsedNanos,
                                       long memoryBefore,
                                       long memoryAfter,
                                       double topRetrievalAvgNs,
                                       Double topRiskScore) {
        double elapsedMs = elapsedNanos / 1_000_000.0;
        double memoryMb = Math.max(0, memoryAfter - memoryBefore) / (1024.0 * 1024.0);

        AlgorithmTestResult result = new AlgorithmTestResult(
                MODULE,
                algorithm,
                inputSize,
                elapsedMs,
                memoryMb
        );
        result.setSolutionMetric(
                "topRetrievalAvgNs=" + String.format("%.2f", topRetrievalAvgNs)
                        + "; topRiskScore=" + topRiskScore
        );
        return result;
    }

    private long usedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private double average(long totalNanos, int count) {
        return count == 0 ? 0.0 : totalNanos / (double) count;
    }
}
