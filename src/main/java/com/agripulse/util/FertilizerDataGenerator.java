package com.agripulse.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.agripulse.model.Farm;
import com.agripulse.model.FertilizerRequest;

/**
 * Generates repeatable synthetic Farm + FertilizerRequest datasets for
 * benchmarking the 0/1 Knapsack DP algorithm against baseline algorithms.
 *
 * A fixed seed is used so the same dataset can be regenerated exactly
 * for report evidence, viva demonstration, or peer verification.
 */
public class FertilizerDataGenerator {

    private static final long DEFAULT_SEED = 42L;

    private static final int MIN_BAGS = 1;
    private static final int MAX_BAGS = 50;

    private static final double MIN_BENEFIT = 1.0;
    private static final double MAX_BENEFIT = 100.0;

    private static final String[] REGIONS = {
            "Kandy", "Nuwara Eliya", "Ratnapura", "Matara", "Badulla"
    };

    private static final String[] FERTILIZER_TYPES = {
            "Urea", "NPK", "Compost", "TSP", "MOP"
    };

    private static final String[] URGENCY_LEVELS = {
            "LOW", "MEDIUM", "HIGH"
    };

    private static final String[] CROP_TYPES = {
            "Tea", "Rubber", "Coconut"
    };

    public static List<FertilizerRequest> generate(int count) {
        return generate(count, DEFAULT_SEED);
    }

    public static List<FertilizerRequest> generate(int count, long seed) {
        Random random = new Random(seed);
        List<FertilizerRequest> requests = new ArrayList<>(count);

        for (int i = 1; i <= count; i++) {
            Farm farm = new Farm(
                    "Farm-" + i,
                    String.format("07%08d", 10000000 + i),
                    REGIONS[random.nextInt(REGIONS.length)],
                    CROP_TYPES[random.nextInt(CROP_TYPES.length)],
                    1.0 + random.nextDouble() * 9.0 // land size between 1.0 and 10.0 acres
            );

            String fertilizerType = FERTILIZER_TYPES[random.nextInt(FERTILIZER_TYPES.length)];
            String urgencyLevel = URGENCY_LEVELS[random.nextInt(URGENCY_LEVELS.length)];

            int requestedBags = MIN_BAGS + random.nextInt(MAX_BAGS - MIN_BAGS + 1);

            double benefitScore = MIN_BENEFIT +
                    (MAX_BENEFIT - MIN_BENEFIT) * random.nextDouble();
            benefitScore = Math.round(benefitScore * 100.0) / 100.0;

            requests.add(new FertilizerRequest(farm, fertilizerType, requestedBags, benefitScore, urgencyLevel));
        }

        return requests;
    }
}