package com.agripulse.backend.service.scheduling;

public record ScheduleComparisonResult(
        ScheduleResult geneticResult,
        ScheduleResult annealingResult,
        String betterMethod,
        double differencePercent,
        String verdict
) {
    public static ScheduleComparisonResult of(ScheduleResult genetic, ScheduleResult annealing) {
        long gaValue = genetic.totalPriorityValue();
        long saValue = annealing.totalPriorityValue();
        long higher = Math.max(gaValue, saValue);
        double diffPercent = higher == 0 ? 0.0 : (Math.abs(gaValue - saValue) * 100.0) / higher;

        String better;
        String verdict;
        if (gaValue == saValue) {
            better = "TIE";
            verdict = String.format(
                    "Both methods reached the same total priority value (%d) for this shift.", gaValue);
        } else if (gaValue > saValue) {
            better = "GENETIC_ALGORITHM";
            verdict = String.format(
                    "The Genetic Algorithm plan is %.1f%% better than the Simulated Annealing plan (%d vs %d priority points) for this shift.",
                    diffPercent, gaValue, saValue);
        } else {
            better = "SIMULATED_ANNEALING";
            verdict = String.format(
                    "The Simulated Annealing plan is %.1f%% better than the Genetic Algorithm plan (%d vs %d priority points) for this shift.",
                    diffPercent, saValue, gaValue);
        }

        return new ScheduleComparisonResult(genetic, annealing, better, diffPercent, verdict);
    }
}
