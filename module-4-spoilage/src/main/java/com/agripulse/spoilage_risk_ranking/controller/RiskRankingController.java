package com.agripulse.spoilage_risk_ranking.controller;

import com.agripulse.spoilage_risk_ranking.model.AlgorithmTestResult;
import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import com.agripulse.spoilage_risk_ranking.repository.HarvestBatchRepository;
import com.agripulse.spoilage_risk_ranking.service.BenchmarkService;
import com.agripulse.spoilage_risk_ranking.service.RiskRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spoilage")
public class RiskRankingController {

    private final HarvestBatchRepository batchRepository;
    private final RiskRankingService riskRankingService;
    private final BenchmarkService benchmarkService;

    @Autowired
    public RiskRankingController(HarvestBatchRepository batchRepository,
                                 RiskRankingService riskRankingService,
                                 BenchmarkService benchmarkService) {
        this.batchRepository = batchRepository;
        this.riskRankingService = riskRankingService;
        this.benchmarkService = benchmarkService;
    }

    /**
     * GET /api/spoilage/ranking?method=merge
     * method can be "merge" (default), "bubble", or "insertion".
     * Returns all "ready" batches ranked highest-risk first, and
     * saves each batch's calculated risk_score back to the database.
     */
    @GetMapping("/ranking")
    public List<HarvestBatch> getRanking(@RequestParam(required = false) String method) {
        List<HarvestBatch> readyBatches = batchRepository.findByStatus("ready");
        List<HarvestBatch> ranked = riskRankingService.rank(readyBatches, method);
        batchRepository.saveAll(ranked); // persist the calculated risk_score values
        return ranked;
    }

    /**
     * POST /api/spoilage/batches
     * Add a batch for testing (Postman: send JSON body matching HarvestBatch fields).
     */
    @PostMapping("/batches")
    public HarvestBatch addBatch(@RequestBody HarvestBatch batch) {
        batch.setStatus("ready");
        return batchRepository.save(batch);
    }

    /**
     * POST /api/spoilage/benchmark?sizes=100,10000,100000
     * Runs Bubble/Insertion/Merge sort at each size and stores timing
     * + memory results. Use the response (or query the
     * algorithm_test_results table directly) to build your report charts.
     * Warning: large sizes like 100000 with Bubble Sort can take a while -
     * that slowness is itself part of what your report should show.
     */
    @PostMapping("/benchmark")
    public List<AlgorithmTestResult> runBenchmark(@RequestParam String sizes) {
        int[] sizeArray = java.util.Arrays.stream(sizes.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
        return benchmarkService.runBenchmark(sizeArray);
    }
}