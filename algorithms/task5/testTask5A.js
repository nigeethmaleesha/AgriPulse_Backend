const {
    scheduleBatches
} = require("./batchScheduler");


// Sample tea batches
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


// Factory has 5 machine hours
const availableHours = 5;


// Generate processing schedule
const result = scheduleBatches(
    batches,
    availableHours
);


// Display result
console.log("====================================");
console.log("AgriPulse - Task 5A");
console.log("Factory Processing Scheduler");
console.log("====================================");

console.log("\nSelected Batches:");

console.table(result.selectedBatches);

console.log("\nSkipped Batches:");

console.table(result.skippedBatches);

console.log("\nAvailable Hours:", result.availableHours);
console.log("Used Hours:", result.usedHours);
console.log("Unused Hours:", result.unusedHours);