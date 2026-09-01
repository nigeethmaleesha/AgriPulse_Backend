package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Simulated Annealing for factory processing &amp; worker shift scheduling.
 *
 * Uses the same permutation-of-tasks encoding and {@link ScheduleDecoder}
 * fitness function as the Genetic Algorithm, so the two are directly
 * comparable on the same problem. Starting from a priority-sorted task
 * order, each iteration swaps two tasks to get a neighbor order; a better
 * neighbor is always accepted, a worse one is accepted with probability
 * exp(delta / temperature) so the search can escape local optima early on,
 * cooling down (accepting fewer worse moves) as iterations progress.
 */
@Service
public class SimulatedAnnealingSchedulerService {

    private static final double INITIAL_TEMPERATURE = 100.0;
    private static final double COOLING_RATE = 0.995;
    private static final int MAX_ITERATIONS = 3000;
    private static final double MIN_TEMPERATURE = 0.01;

    private final ScheduleDecoder decoder;

    public SimulatedAnnealingSchedulerService(ScheduleDecoder decoder) {
        this.decoder = decoder;
    }

    public ScheduleResult schedule(List<ProductionTask> tasks, List<Worker> workers,
                                    List<Machine> machines, List<PowerOutage> outages) {
        return schedule(tasks, workers, machines, outages, new Random(System.nanoTime()));
    }

    ScheduleResult schedule(List<ProductionTask> tasks, List<Worker> workers,
                             List<Machine> machines, List<PowerOutage> outages, Random random) {
        long started = System.nanoTime();
        int n = tasks.size();

        if (n == 0) {
            long elapsed = System.nanoTime() - started;
            return ScheduleResult.of("SIMULATED_ANNEALING", List.of(), List.of(), 0L, 0, elapsed);
        }

        int[] current = priorityOrder(tasks);
        long currentFitness = decoder.fitness(current, tasks, workers, machines, outages);

        int[] best = current.clone();
        long bestFitness = currentFitness;

        double temperature = INITIAL_TEMPERATURE;
        for (int iteration = 0; iteration < MAX_ITERATIONS && temperature > MIN_TEMPERATURE; iteration++) {
            int[] neighbor = current.clone();
            if (n > 1) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);
                int tmp = neighbor[a];
                neighbor[a] = neighbor[b];
                neighbor[b] = tmp;
            }

            long neighborFitness = decoder.fitness(neighbor, tasks, workers, machines, outages);
            long delta = neighborFitness - currentFitness;

            if (delta >= 0 || random.nextDouble() < Math.exp(delta / temperature)) {
                current = neighbor;
                currentFitness = neighborFitness;
                if (currentFitness > bestFitness) {
                    bestFitness = currentFitness;
                    best = current.clone();
                }
            }

            temperature *= COOLING_RATE;
        }

        long elapsed = System.nanoTime() - started;
        ScheduleDecoder.DecodedSchedule decoded = decoder.decode(best, tasks, workers, machines, outages);
        return ScheduleResult.of("SIMULATED_ANNEALING", decoded.scheduled(), decoded.unscheduled(),
                decoded.totalValue(), n, elapsed);
    }

    private int[] priorityOrder(List<ProductionTask> tasks) {
        Integer[] indexes = new Integer[tasks.size()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        java.util.Arrays.sort(indexes, (a, b) -> Integer.compare(tasks.get(b).priority(), tasks.get(a).priority()));
        int[] result = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            result[i] = indexes[i];
        }
        return result;
    }
}
