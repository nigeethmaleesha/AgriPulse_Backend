package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns one candidate task order (a permutation of task indexes - the
 * "chromosome" for the Genetic Algorithm, and the perturbed state for
 * Simulated Annealing) into a concrete, constraint-respecting schedule.
 *
 * For each task in the given order, this places it at the earliest hour
 * where: the whole task avoids every recurring daily power outage, a worker
 * with enough remaining working hours is free, and an available machine is
 * free. A task that cannot be placed within the planning horizon is left
 * unscheduled. Both metaheuristics search over task orders; this decoder is
 * the fitness function they optimize against (total priority value of the
 * tasks it manages to place).
 */
@Component
public class ScheduleDecoder {

    public static final int PLANNING_HORIZON_HOURS = 48;

    public long fitness(int[] order, List<ProductionTask> tasks, List<Worker> workers,
                         List<Machine> machines, List<PowerOutage> outages) {
        return decode(order, tasks, workers, machines, outages).totalValue();
    }

    public DecodedSchedule decode(int[] order, List<ProductionTask> tasks, List<Worker> workers,
                                   List<Machine> machines, List<PowerOutage> outages) {
        int workerCount = workers.size();
        int machineCount = machines.size();

        boolean[] outageHourOfDay = new boolean[24];
        for (PowerOutage outage : outages) {
            for (int h = outage.startHour(); h < outage.endHour(); h++) {
                outageHourOfDay[h] = true;
            }
        }

        boolean[][] workerBusy = new boolean[workerCount][PLANNING_HORIZON_HOURS];
        boolean[][] machineBusy = new boolean[machineCount][PLANNING_HORIZON_HOURS];
        int[] workerRemainingHours = new int[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workerRemainingHours[i] = workers.get(i).maxWorkingHours();
        }

        List<ScheduleEntry> scheduled = new ArrayList<>();
        List<ProductionTask> unscheduled = new ArrayList<>();
        long totalValue = 0L;

        for (int taskIndex : order) {
            ProductionTask task = tasks.get(taskIndex);
            int duration = task.processingTimeHours();
            int placedStart = -1;
            int placedWorker = -1;
            int placedMachine = -1;

            searchStart:
            for (int start = 0; start <= PLANNING_HORIZON_HOURS - duration; start++) {
                if (overlapsOutage(outageHourOfDay, start, duration)) {
                    continue;
                }
                for (int w = 0; w < workerCount; w++) {
                    if (workerRemainingHours[w] < duration || !isFree(workerBusy[w], start, duration)) {
                        continue;
                    }
                    for (int m = 0; m < machineCount; m++) {
                        if (!machines.get(m).available() || !isFree(machineBusy[m], start, duration)) {
                            continue;
                        }
                        placedStart = start;
                        placedWorker = w;
                        placedMachine = m;
                        break searchStart;
                    }
                }
            }

            if (placedStart >= 0) {
                markBusy(workerBusy[placedWorker], placedStart, duration);
                markBusy(machineBusy[placedMachine], placedStart, duration);
                workerRemainingHours[placedWorker] -= duration;
                scheduled.add(new ScheduleEntry(task, workers.get(placedWorker), machines.get(placedMachine),
                        placedStart, placedStart + duration));
                totalValue += task.priority();
            } else {
                unscheduled.add(task);
            }
        }

        return new DecodedSchedule(scheduled, unscheduled, totalValue);
    }

    private boolean overlapsOutage(boolean[] outageHourOfDay, int start, int duration) {
        for (int h = start; h < start + duration; h++) {
            if (outageHourOfDay[h % 24]) {
                return true;
            }
        }
        return false;
    }

    private boolean isFree(boolean[] busy, int start, int duration) {
        for (int h = start; h < start + duration; h++) {
            if (busy[h]) {
                return false;
            }
        }
        return true;
    }

    private void markBusy(boolean[] busy, int start, int duration) {
        for (int h = start; h < start + duration; h++) {
            busy[h] = true;
        }
    }

    public record DecodedSchedule(List<ScheduleEntry> scheduled, List<ProductionTask> unscheduled, long totalValue) {
    }
}
