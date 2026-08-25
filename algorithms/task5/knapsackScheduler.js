// Task 5A - 0/1 Knapsack Scheduler
// AgriPulse Intelligent Decision Support System

/**
 * Select the best combination of tea batches
 * within the available machine hours.
 *
 * Each batch can either be:
 * 0 = not selected
 * 1 = selected
 *
 * Weight  = processing time
 * Value   = priority score
 * Capacity = available machine hours
 */

function knapsackScheduler(batches, availableHours) {

    const n = batches.length;

    // DP table
    // dp[i][h] = maximum priority achievable
    // using first i batches with h available hours
    const dp = Array.from(
        { length: n + 1 },
        () => Array(availableHours + 1).fill(0)
    );

    // Build the DP table
    for (let i = 1; i <= n; i++) {

        const batch = batches[i - 1];

        for (let hours = 0; hours <= availableHours; hours++) {

            // If the batch cannot fit
            if (batch.processingTime > hours) {

                dp[i][hours] = dp[i - 1][hours];

            } else {

                // Option 1: Don't select the batch
                const excludeBatch = dp[i - 1][hours];

                // Option 2: Select the batch
                const includeBatch =
                    batch.priorityScore +
                    dp[i - 1][hours - batch.processingTime];

                // Select whichever gives higher priority
                dp[i][hours] = Math.max(
                    excludeBatch,
                    includeBatch
                );
            }
        }
    }

    // Find which batches were selected
    const selectedBatches = [];

    let remainingHours = availableHours;

    for (let i = n; i > 0; i--) {

        if (dp[i][remainingHours] !== dp[i - 1][remainingHours]) {

            const batch = batches[i - 1];

            selectedBatches.push({
                batchId: batch.batchId,
                priorityScore: batch.priorityScore,
                processingTime: batch.processingTime,
                quantity: batch.quantity
            });

            remainingHours -= batch.processingTime;
        }
    }

    // Reverse so the schedule is in processing order
    selectedBatches.reverse();

    const usedHours =
        availableHours - remainingHours;

    // Identify batches that were not selected
    const selectedIds = new Set(
        selectedBatches.map(batch => batch.batchId)
    );

    const skippedBatches = batches.filter(
        batch => !selectedIds.has(batch.batchId)
    );

    const totalPriority = selectedBatches.reduce(
        (total, batch) => total + batch.priorityScore,
        0
    );

    return {
        selectedBatches,
        skippedBatches,
        availableHours,
        usedHours,
        unusedHours: remainingHours,
        totalPriority
    };
}


module.exports = {
    knapsackScheduler
};