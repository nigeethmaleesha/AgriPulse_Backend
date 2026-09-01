package com.agripulse.spoilage_risk_ranking.service;

import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;
import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import com.agripulse.spoilage_risk_ranking.repository.AlgorithmTestResultRepository;
import com.agripulse.spoilage_risk_ranking.util.SyntheticBatchGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Runs Bubble Sort, Insertion Sort, and Merge Sort on the same
 * synthetic dataset at each requested size, times them, and saves
 * the numbers into algorithm_test_results. This is the "experimental
 * evaluation" evidence your report needs - real measurements, not
 * guesses.
 */
@Service
public class BenchmarkService {

    private final RiskRankingService riskRankingService;
    private final AlgorithmTestResultRepository resultRepository;

    private static final String MODULE = "spoilage_ranking";

    @Autowired
    public BenchmarkService(RiskRankingService riskRankingService,
                            AlgorithmTestResultRepository resultRepository) {
        this.riskRankingService = riskRankingService;
        this.resultRepository = resultRepository;
    }

    /** Runs all three algorithms at every given input size and saves the results. */
    public List<AlgorithmTestResult> runBenchmark(int[] sizes) {
        for (int size : sizes) {
            // Use the SAME seed for all three algorithms at a given size,
            // so they're compared on identical data - a fair test.
            List<HarvestBatch> data = SyntheticBatchGenerator.generate(size, 42L);
            riskRankingService.scoreAll(data);

            timeAndSave("bubble", size, () -> riskRankingService.bubbleSort(data));
            timeAndSave("insertion", size, () -> riskRankingService.insertionSort(data));
            timeAndSave("merge", size, () -> riskRankingService.mergeSort(data));
        }
        return resultRepository.findByModule(MODULE);
    }

    private void timeAndSave(String algorithmName, int size, Runnable sortCall) {
        Runtime runtime = Runtime.getRuntime();

        runtime.gc(); // ask the JVM to clean up first so the memory reading is less noisy
        long memBefore = runtime.totalMemory() - runtime.freeMemory();
        long start = System.nanoTime();

        sortCall.run();

        long elapsedNanos = System.nanoTime() - start;
        long memAfter = runtime.totalMemory() - runtime.freeMemory();

        double elapsedMs = elapsedNanos / 1_000_000.0;
        double memoryMb = Math.max(0, (memAfter - memBefore)) / (1024.0 * 1024.0);

        AlgorithmTestResult result = new AlgorithmTestResult(MODULE, algorithmName, size, elapsedMs, memoryMb);
        resultRepository.save(result);
    }
}