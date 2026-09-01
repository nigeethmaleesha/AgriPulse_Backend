package com.agripulse.backend.service.scheduling;

import java.util.ArrayList;
import java.util.List;

public class FactorySchedulingServiceTest {

    public static void main(String[] args) {

        FactorySchedulingService service =
                new FactorySchedulingService();

        // -----------------------------
        // 1. Create production tasks
        // -----------------------------
        List<ProductionTask> tasks = new ArrayList<>();

        tasks.add(new ProductionTask(
                "T1",
                "Rolling Tea Leaves",
                3,
                4
        ));

        tasks.add(new ProductionTask(
                "T2",
                "Plucking Tea Leaves",
                2,
                3
        ));

        tasks.add(new ProductionTask(
                "T3",
                "Tea Processing",
                1,
                5
        ));

        // -----------------------------
        // 2. Create workers
        // -----------------------------
        List<Worker> workers = new ArrayList<>();

        workers.add(new Worker(
                "W1",
                "Kamal",
                "Day",
                8
        ));

        workers.add(new Worker(
                "W2",
                "Nimal",
                "Day",
                8
        ));

        // -----------------------------
        // 3. Create machines
        // -----------------------------
        List<Machine> machines = new ArrayList<>();

        machines.add(new Machine(
                "M1",
                "Tea Rolling Machine",
                10,
                true
        ));

        machines.add(new Machine(
                "M2",
                "Tea Processing Machine",
                10,
                true
        ));

        // -----------------------------
        // 4. Create power outages
        // -----------------------------
        List<PowerOutage> outages = new ArrayList<>();

outages.add(new PowerOutage(
        "O1",
        4,
        6
));

        // -----------------------------
        // 5. Generate schedule
        // -----------------------------
        List<ScheduleEntry> schedule =
                service.generateSchedule(
                        tasks,
                        workers,
                        machines,
                        outages
                );

        // -----------------------------
        // 6. Display result
        // -----------------------------
        System.out.println();
        System.out.println("======================================");
        System.out.println("       AGRIPULSE FACTORY SCHEDULE");
        System.out.println("======================================");

        for (ScheduleEntry entry : schedule) {

            System.out.println(
                    "Task       : " +
                    entry.getTask().getTaskName()
            );

            System.out.println(
                    "Worker     : " +
                    entry.getWorker().getWorkerName()
            );

            System.out.println(
                    "Machine    : " +
                    entry.getMachine().getMachineName()
            );

            System.out.println(
                    "Start Hour : " +
                    entry.getStartHour()
            );

            System.out.println(
                    "End Hour   : " +
                    entry.getEndHour()
            );

            System.out.println("--------------------------------------");
        }

        System.out.println("Total tasks scheduled: " + schedule.size());
        System.out.println("======================================");
    }
}