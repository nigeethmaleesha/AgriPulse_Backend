package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Repeatable synthetic scenario generator for GA/SA experimental testing. */
@Component
public class SyntheticSchedulingGenerator {

    public record Scenario(List<ProductionTask> tasks, List<Worker> workers, List<Machine> machines,
                            List<PowerOutage> outages) {
    }

    public Scenario generate(int taskCount, long seed) {
        if (taskCount <= 0) {
            throw new IllegalArgumentException("taskCount must be greater than 0");
        }
        Random random = new Random(seed);

        List<ProductionTask> tasks = new ArrayList<>(taskCount);
        for (int i = 1; i <= taskCount; i++) {
            tasks.add(new ProductionTask("T" + i, "Synthetic Task " + i, 1 + random.nextInt(4), 1 + random.nextInt(10)));
        }

        int workerCount = Math.max(2, taskCount / 3);
        List<Worker> workers = new ArrayList<>(workerCount);
        String[] shifts = {"Morning", "Evening", "Night"};
        for (int i = 1; i <= workerCount; i++) {
            workers.add(new Worker("W" + i, "Worker " + i, shifts[i % shifts.length], 6 + random.nextInt(4)));
        }

        int machineCount = Math.max(2, taskCount / 8);
        List<Machine> machines = new ArrayList<>(machineCount);
        for (int i = 1; i <= machineCount; i++) {
            machines.add(new Machine("M" + i, "Machine " + i, true));
        }

        List<PowerOutage> outages = List.of(new PowerOutage("O1", 12, 14));

        return new Scenario(tasks, workers, machines, outages);
    }
}
