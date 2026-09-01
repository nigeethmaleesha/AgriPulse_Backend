package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Genetic Algorithm for factory processing &amp; worker shift scheduling.
 *
 * A candidate solution ("chromosome") is a permutation of task indexes - the
 * order tasks are offered to {@link ScheduleDecoder}. The decoder places
 * each task at the earliest feasible worker/machine/hour, so every
 * chromosome always decodes to a valid, constraint-respecting schedule;
 * the GA searches for the task order that maximizes total priority value
 * scheduled.
 *
 * Operators: tournament selection, order crossover (OX, preserves relative
 * order so decoded schedules stay meaningful), swap mutation, and elitism.
 */
@Service
public class GeneticAlgorithmSchedulerService {

    private static final int POPULATION_SIZE = 40;
    private static final int GENERATIONS = 60;
    private static final int TOURNAMENT_SIZE = 3;
    private static final double CROSSOVER_RATE = 0.9;
    private static final double MUTATION_RATE = 0.15;
    private static final int ELITE_COUNT = 2;

    private final ScheduleDecoder decoder;

    public GeneticAlgorithmSchedulerService(ScheduleDecoder decoder) {
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
            return ScheduleResult.of("GENETIC_ALGORITHM", List.of(), List.of(), 0L, 0, elapsed);
        }

        int[][] population = new int[POPULATION_SIZE][];
        population[0] = priorityOrder(tasks);
        for (int i = 1; i < POPULATION_SIZE; i++) {
            population[i] = randomPermutation(n, random);
        }

        int[] best = population[0];
        long bestFitness = decoder.fitness(best, tasks, workers, machines, outages);

        for (int generation = 0; generation < GENERATIONS; generation++) {
            long[] fitness = new long[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                fitness[i] = decoder.fitness(population[i], tasks, workers, machines, outages);
                if (fitness[i] > bestFitness) {
                    bestFitness = fitness[i];
                    best = population[i];
                }
            }

            int[][] nextGeneration = new int[POPULATION_SIZE][];
            List<Integer> eliteIndexes = eliteIndexes(fitness, ELITE_COUNT);
            for (int i = 0; i < eliteIndexes.size(); i++) {
                nextGeneration[i] = population[eliteIndexes.get(i)].clone();
            }

            for (int i = eliteIndexes.size(); i < POPULATION_SIZE; i++) {
                int[] parentA = tournamentSelect(population, fitness, random);
                int[] parentB = tournamentSelect(population, fitness, random);
                int[] child = random.nextDouble() < CROSSOVER_RATE
                        ? orderCrossover(parentA, parentB, random)
                        : parentA.clone();
                if (random.nextDouble() < MUTATION_RATE) {
                    swapMutate(child, random);
                }
                nextGeneration[i] = child;
            }
            population = nextGeneration;
        }

        long elapsed = System.nanoTime() - started;
        ScheduleDecoder.DecodedSchedule decoded = decoder.decode(best, tasks, workers, machines, outages);
        return ScheduleResult.of("GENETIC_ALGORITHM", decoded.scheduled(), decoded.unscheduled(),
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

    private int[] randomPermutation(int n, Random random) {
        int[] order = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
        return order;
    }

    private int[] tournamentSelect(int[][] population, long[] fitness, Random random) {
        int bestIndex = random.nextInt(population.length);
        for (int i = 1; i < TOURNAMENT_SIZE; i++) {
            int challenger = random.nextInt(population.length);
            if (fitness[challenger] > fitness[bestIndex]) {
                bestIndex = challenger;
            }
        }
        return population[bestIndex];
    }

    private int[] orderCrossover(int[] parentA, int[] parentB, Random random) {
        int n = parentA.length;
        int[] child = new int[n];
        java.util.Arrays.fill(child, -1);

        int start = random.nextInt(n);
        int end = random.nextInt(n);
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }

        boolean[] used = new boolean[n];
        for (int i = start; i <= end; i++) {
            child[i] = parentA[i];
            used[parentA[i]] = true;
        }

        int insertPos = (end + 1) % n;
        for (int offset = 0; offset < n; offset++) {
            int candidate = parentB[(end + 1 + offset) % n];
            if (!used[candidate]) {
                child[insertPos] = candidate;
                used[candidate] = true;
                insertPos = (insertPos + 1) % n;
            }
        }
        return child;
    }

    private void swapMutate(int[] chromosome, Random random) {
        int a = random.nextInt(chromosome.length);
        int b = random.nextInt(chromosome.length);
        int tmp = chromosome[a];
        chromosome[a] = chromosome[b];
        chromosome[b] = tmp;
    }

    private List<Integer> eliteIndexes(long[] fitness, int count) {
        Integer[] indexes = new Integer[fitness.length];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }
        java.util.Arrays.sort(indexes, (a, b) -> Long.compare(fitness[b], fitness[a]));
        return List.of(indexes).subList(0, Math.min(count, indexes.length));
    }
}
