const {
    scheduleBatches
} = require("./batchScheduler");

const {
    knapsackScheduler
} = require("./knapsackScheduler");


const batches = [
    {
        batchId: "B001",
        priorityScore: 90,
        processingTime: 2,
        quantity: 500
    },
    {
        batchId: "B002",
        priorityScore: 70,
        processingTime: 1,
        quantity: 300
    },
    {
        batchId: "B003",
        priorityScore: 95,
        processingTime: 3,
        quantity: 700
    },
    {
        batchId: "B004",
        priorityScore: 60,
        processingTime: 2,
        quantity: 400
    }
];


const availableHours = 5;


// -----------------------------
// Max Heap Scheduler
// -----------------------------

const heapResult = scheduleBatches(
    batches,
    availableHours
);


// Calculate Heap total priority
const heapPriority = heapResult.selectedBatches.reduce(
    (total, batch) => total + batch.priorityScore,
    0
);


// -----------------------------
// 0/1 Knapsack Scheduler
// -----------------------------

const knapsackResult = knapsackScheduler(
    batches,
    availableHours
);


// -----------------------------
// Display comparison
// -----------------------------

console.log("\n========================================");
console.log("AgriPulse - Task 5A");
console.log("Algorithm Comparison");
console.log("========================================");

console.log("\nMAX HEAP RESULT");
console.table(heapResult.selectedBatches);

console.log("Total Priority:", heapPriority);
console.log("Used Hours:", heapResult.usedHours);


console.log("\n0/1 KNAPSACK RESULT");
console.table(knapsackResult.selectedBatches);

console.log("Total Priority:", knapsackResult.totalPriority);
console.log("Used Hours:", knapsackResult.usedHours);


console.log("\n========================================");
console.log("FINAL COMPARISON");
console.log("========================================");

console.table([
    {
        algorithm: "Max Heap",
        totalPriority: heapPriority,
        usedHours: heapResult.usedHours
    },
    {
        algorithm: "0/1 Knapsack",
        totalPriority: knapsackResult.totalPriority,
        usedHours: knapsackResult.usedHours
    }
]);