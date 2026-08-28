package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FactorySchedulingService {

    /**
     * Generates a factory production schedule.
     *
     * Scheduling considers:
     * - Task priority
     * - Worker working-hour limits
     * - Machine availability
     * - Existing worker assignments
     * - Existing machine assignments
     * - Power outages
     */
    public List<ScheduleEntry> generateSchedule(
            List<ProductionTask> tasks,
            List<Worker> workers,
            List<Machine> machines,
            List<PowerOutage> outages) {

        List<ScheduleEntry> schedule = new ArrayList<>();

        // -------------------------------------------------
        // 1. Process high-priority tasks first
        // -------------------------------------------------
        tasks.sort(
                Comparator.comparingInt(
                        ProductionTask::getPriority
                ).reversed()
        );

        int currentHour = 0;

        // -------------------------------------------------
        // 2. Schedule each production task
        // -------------------------------------------------
        for (ProductionTask task : tasks) {

            int processingTime = task.getProcessingTime();

            // -------------------------------------------------
            // 3. Find the earliest possible starting hour
            // -------------------------------------------------
            int startHour = findAvailableStartHour(
                    currentHour,
                    processingTime,
                    outages
            );

            int endHour = startHour + processingTime;

            // -------------------------------------------------
            // 4. Find an available worker
            // -------------------------------------------------
            Worker selectedWorker = findAvailableWorker(
                    workers,
                    schedule,
                    startHour,
                    endHour,
                    processingTime
            );

            // If no suitable worker exists, skip this task
            if (selectedWorker == null) {
                continue;
            }

            // -------------------------------------------------
            // 5. Find an available machine
            // -------------------------------------------------
            Machine selectedMachine = findAvailableMachine(
                    machines,
                    schedule,
                    startHour,
                    endHour
            );

            // If no suitable machine exists, skip this task
            if (selectedMachine == null) {
                continue;
            }

            // -------------------------------------------------
            // 6. Create schedule entry
            // -------------------------------------------------
            ScheduleEntry entry = new ScheduleEntry(
                    task,
                    selectedWorker,
                    selectedMachine,
                    startHour,
                    endHour
            );

            // -------------------------------------------------
            // 7. Deduct worker working hours
            // -------------------------------------------------
            selectedWorker.assignHours(processingTime);

            // -------------------------------------------------
            // 8. Add task to final schedule
            // -------------------------------------------------
            schedule.add(entry);

            // -------------------------------------------------
            // 9. Move scheduling time forward
            // -------------------------------------------------
            currentHour = endHour;
        }

        return schedule;
    }

    /**
     * Finds an available machine that is not already
     * assigned to another task during the requested period.
     */
    private Machine findAvailableMachine(
            List<Machine> machines,
            List<ScheduleEntry> schedule,
            int startHour,
            int endHour) {

        for (Machine machine : machines) {

            // Check whether the machine is available
            if (!machine.isAvailable()) {
                continue;
            }

            boolean machineBusy = false;

            // Check existing schedule entries
            for (ScheduleEntry entry : schedule) {

                // Check whether the same machine is being used
                if (entry.getMachine()
                        .getMachineId()
                        .equals(machine.getMachineId())) {

                    // Check whether the time periods overlap
                    if (startHour < entry.getEndHour()
                            && endHour > entry.getStartHour()) {

                        machineBusy = true;
                        break;
                    }
                }
            }

            // Machine is free during this period
            if (!machineBusy) {
                return machine;
            }
        }

        return null;
    }

    /**
     * Finds a worker who:
     * - Has enough remaining working hours
     * - Is not already assigned to another task
     *   during the requested time period
     */
    private Worker findAvailableWorker(
            List<Worker> workers,
            List<ScheduleEntry> schedule,
            int startHour,
            int endHour,
            int requiredHours) {

        for (Worker worker : workers) {

            // Check worker's remaining working hours
            if (!worker.canWork(requiredHours)) {
                continue;
            }

            boolean workerBusy = false;

            // Check existing schedule entries
            for (ScheduleEntry entry : schedule) {

                // Check whether this worker is already assigned
                if (entry.getWorker()
                        .getWorkerId()
                        .equals(worker.getWorkerId())) {

                    // Check whether the time periods overlap
                    if (startHour < entry.getEndHour()
                            && endHour > entry.getStartHour()) {

                        workerBusy = true;
                        break;
                    }
                }
            }

            // Worker is available
            if (!workerBusy) {
                return worker;
            }
        }

        return null;
    }

    /**
     * Finds the earliest starting hour where the complete
     * task does not overlap with a power outage.
     */
    private int findAvailableStartHour(
            int startHour,
            int processingTime,
            List<PowerOutage> outages) {

        int hour = startHour;

        while (hasPowerOutage(
                hour,
                processingTime,
                outages)) {

            // Move forward one hour and check again
            hour++;
        }

        return hour;
    }

    /**
     * Checks whether the task overlaps with any
     * power outage.
     */
    private boolean hasPowerOutage(
            int startHour,
            int processingTime,
            List<PowerOutage> outages) {

        int endHour = startHour + processingTime;

        for (PowerOutage outage : outages) {

            for (int hour = startHour;
                    hour < endHour;
                    hour++) {

                if (outage.affectsHour(hour)) {
                    return true;
                }
            }
        }

        return false;
    }
}