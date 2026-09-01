package com.agripulse.spoilage_risk_ranking.util;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates fake-but-realistic harvest batches for benchmarking, so
 * you can test with 100, 10,000, 100,000 batches without needing
 * real plantation data. A fixed seed means the results are repeatable
 * every time you re-run the benchmark (important for a fair report).
 */
public class SyntheticBatchGenerator {

    public static List<HarvestBatch> generate(int count, long seed) {
        Random random = new Random(seed);
        List<HarvestBatch> batches = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            HarvestBatch batch = new HarvestBatch();
            batch.setFarmId((long) (random.nextInt(50) + 1));
            batch.setCollectionPointId((long) (random.nextInt(10) + 1));
            batch.setQuantity(20 + random.nextDouble() * 200);           // 20-220 kg
            batch.setHarvestTime(LocalDateTime.now().minusMinutes(random.nextInt(600))); // up to 10h ago
            batch.setTemperature(18 + random.nextDouble() * 18);         // 18-36 C
            batch.setHumidity(50 + random.nextDouble() * 45);            // 50-95 %
            batch.setStatus("ready");
            batches.add(batch);
        }
        return batches;
    }
}