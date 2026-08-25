// Task 5A - Factory Batch Processing Scheduler
// AgriPulse Intelligent Decision Support System

class MaxHeap {
    constructor() {
        this.heap = [];
    }

    parent(index) {
        return Math.floor((index - 1) / 2);
    }

    leftChild(index) {
        return index * 2 + 1;
    }

    rightChild(index) {
        return index * 2 + 2;
    }

    swap(index1, index2) {
        [this.heap[index1], this.heap[index2]] =
            [this.heap[index2], this.heap[index1]];
    }

    // Add a batch to the priority queue
    insert(batch) {
        this.heap.push(batch);

        let currentIndex = this.heap.length - 1;

        while (currentIndex > 0) {
            const parentIndex = this.parent(currentIndex);

            if (
                this.heap[parentIndex].priorityScore >=
                this.heap[currentIndex].priorityScore
            ) {
                break;
            }

            this.swap(parentIndex, currentIndex);
            currentIndex = parentIndex;
        }
    }

    // Remove the highest-priority batch
    extractMax() {
        if (this.heap.length === 0) {
            return null;
        }

        if (this.heap.length === 1) {
            return this.heap.pop();
        }

        const maxBatch = this.heap[0];

        this.heap[0] = this.heap.pop();

        this.heapifyDown(0);

        return maxBatch;
    }

    heapifyDown(index) {
        while (true) {
            let largest = index;

            const left = this.leftChild(index);
            const right = this.rightChild(index);

            if (
                left < this.heap.length &&
                this.heap[left].priorityScore >
                    this.heap[largest].priorityScore
            ) {
                largest = left;
            }

            if (
                right < this.heap.length &&
                this.heap[right].priorityScore >
                    this.heap[largest].priorityScore
            ) {
                largest = right;
            }

            if (largest === index) {
                break;
            }

            this.swap(index, largest);
            index = largest;
        }
    }

    isEmpty() {
        return this.heap.length === 0;
    }

    size() {
        return this.heap.length;
    }
}


/**
 * Task 5A
 * Dynamic Factory Batch Processing Scheduler
 *
 * @param {Array} batches - Available tea batches
 * @param {number} availableHours - Available machine hours
 * @returns {Object} Processing schedule
 */
function scheduleBatches(batches, availableHours) {

    const priorityQueue = new MaxHeap();

    // Add all batches to the priority queue
    for (const batch of batches) {
        priorityQueue.insert(batch);
    }

    const selectedBatches = [];
    const skippedBatches = [];

    let remainingHours = availableHours;
    let usedHours = 0;

    // Continue until all batches have been considered
    while (!priorityQueue.isEmpty()) {

        const batch = priorityQueue.extractMax();

        // Check whether the batch fits in the remaining machine time
        if (batch.processingTime <= remainingHours) {

            selectedBatches.push({
                batchId: batch.batchId,
                priorityScore: batch.priorityScore,
                processingTime: batch.processingTime,
                quantity: batch.quantity
            });

            usedHours += batch.processingTime;
            remainingHours -= batch.processingTime;

        } else {

            // Batch cannot fit in the remaining time
            skippedBatches.push(batch);
        }
    }

    return {
        selectedBatches,
        skippedBatches,
        availableHours,
        usedHours,
        unusedHours: remainingHours
    };
}


module.exports = {
    MaxHeap,
    scheduleBatches
};