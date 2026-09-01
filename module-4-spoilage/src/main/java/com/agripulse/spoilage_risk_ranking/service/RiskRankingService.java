package com.agripulse.spoilage_risk_ranking.service;

import com.agripulse.spoilage_risk_ranking.model.HarvestBatch;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Member 7 scope: calculate a risk score for every batch, then rank
 * all batches from highest to lowest risk using three different
 * sorting algorithms so they can be compared in the report.
 *
 * Risk formula (project-defined assumption - document this in your
 * report exactly as written here, since the coursework brief does
 * not supply fixed weights):
 *
 *   riskScore = (hoursSinceHarvest * 0.40)
 *             + (temperature      * 0.35)
 *             + (humidity         * 0.25 / 10)
 *
 * Higher score = higher spoilage risk. The weights say: how long the
 * batch has waited matters most, heat matters almost as much, and
 * humidity (scaled down since it's 0-100 vs the other smaller ranges)
 * contributes a bit less.
 */
@Service
public class RiskRankingService {

    // ---------- STEP 1: risk score calculation ----------

    public double calculateRiskScore(HarvestBatch batch) {
        double hours = batch.hoursSinceHarvest();
        double temp = batch.getTemperature() == null ? 0 : batch.getTemperature();
        double humidity = batch.getHumidity() == null ? 0 : batch.getHumidity();

        double score = (hours * 0.40) + (temp * 0.35) + ((humidity / 10.0) * 0.25);
        return Math.round(score * 100.0) / 100.0; // round to 2 decimal places
    }

    /** Sets riskScore on every batch in the list (does not sort). */
    public void scoreAll(List<HarvestBatch> batches) {
        for (HarvestBatch batch : batches) {
            batch.setRiskScore(calculateRiskScore(batch));
        }
    }

    // ---------- STEP 2: three sorting algorithms ----------
    // All three sort DESCENDING by riskScore (highest risk first).
    // Each returns a NEW list so the original input is untouched -
    // that makes it safe to run all three back-to-back on the same data.

    /** O(n^2) - simplest possible sort: repeatedly swap adjacent
     *  out-of-order elements until no swaps are needed. */
    public List<HarvestBatch> bubbleSort(List<HarvestBatch> input) {
        List<HarvestBatch> list = new ArrayList<>(input);
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (list.get(j).getRiskScore() < list.get(j + 1).getRiskScore()) {
                    HarvestBatch temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break; // already sorted, stop early
        }
        return list;
    }

    /** O(n^2) worst case, but fast on nearly-sorted data - builds the
     *  sorted list one element at a time by inserting each new item
     *  into its correct position. */
    public List<HarvestBatch> insertionSort(List<HarvestBatch> input) {
        List<HarvestBatch> list = new ArrayList<>(input);
        for (int i = 1; i < list.size(); i++) {
            HarvestBatch key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).getRiskScore() < key.getRiskScore()) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }

    /** O(n log n) - divide the list in half recursively, sort each
     *  half, then merge the two sorted halves back together. */
    public List<HarvestBatch> mergeSort(List<HarvestBatch> input) {
        List<HarvestBatch> list = new ArrayList<>(input);
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        List<HarvestBatch> left = mergeSort(list.subList(0, mid));
        List<HarvestBatch> right = mergeSort(list.subList(mid, list.size()));
        return merge(left, right);
    }

    private List<HarvestBatch> merge(List<HarvestBatch> left, List<HarvestBatch> right) {
        List<HarvestBatch> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getRiskScore() >= right.get(j).getRiskScore()) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    // ---------- STEP 3: pick a sort by name (used by the controller) ----------

    public List<HarvestBatch> rank(List<HarvestBatch> batches, String method) {
        scoreAll(batches);
        return switch (method == null ? "merge" : method.toLowerCase()) {
            case "bubble" -> bubbleSort(batches);
            case "insertion" -> insertionSort(batches);
            default -> mergeSort(batches); // "merge" is the recommended primary method
        };
    }
}