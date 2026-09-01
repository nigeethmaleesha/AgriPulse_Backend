package com.agripulse.backend.service.scheduling;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GeneticAlgorithmSchedulerServiceTest {

    private final GeneticAlgorithmSchedulerService service = new GeneticAlgorithmSchedulerService(new ScheduleDecoder());

    @Test
    void findsAScheduleAtLeastAsGoodAsThePriorityOrderBaseline() {
        List<ProductionTask> tasks = List.of(
                new ProductionTask("T1", "Rolling Tea Leaves", 3, 4),
                new ProductionTask("T2", "Plucking Tea Leaves", 2, 3),
                new ProductionTask("T3", "Tea Processing", 1, 5),
                new ProductionTask("T4", "Sorting", 2, 8),
                new ProductionTask("T5", "Packing", 1, 6)
        );
        List<Worker> workers = List.of(new Worker("W1", "Kamal", "Day", 8), new Worker("W2", "Nimal", "Day", 8));
        List<Machine> machines = List.of(new Machine("M1", "Tea Rolling Machine", true), new Machine("M2", "Tea Processing Machine", true));
        List<PowerOutage> outages = List.of(new PowerOutage("O1", 4, 6));

        ScheduleResult result = service.schedule(tasks, workers, machines, outages, new Random(1));

        long baselineValue = new ScheduleDecoder()
                .fitness(new int[]{3, 4, 2, 0, 1}, tasks, workers, machines, outages); // priority-sorted order T4,T5,T3,T1,T2
        assertTrue(result.totalPriorityValue() >= baselineValue);
        assertEquals("GENETIC_ALGORITHM", result.method());
    }

    @Test
    void emptyTaskListProducesEmptySchedule() {
        ScheduleResult result = service.schedule(List.of(),
                List.of(new Worker("W1", "Kamal", "Day", 8)),
                List.of(new Machine("M1", "Roller", true)),
                List.of(), new Random(1));
        assertTrue(result.scheduledEntries().isEmpty());
        assertEquals(0, result.totalPriorityValue());
    }
}
