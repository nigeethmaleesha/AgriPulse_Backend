package com.agripulse.backend.service.scheduling;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDecoderTest {

    private final ScheduleDecoder decoder = new ScheduleDecoder();

    @Test
    void schedulesTasksAvoidingOutageAndRespectingWorkerHours() {
        List<ProductionTask> tasks = List.of(
                new ProductionTask("T1", "Rolling Tea Leaves", 3, 4),
                new ProductionTask("T2", "Plucking Tea Leaves", 2, 3)
        );
        List<Worker> workers = List.of(new Worker("W1", "Kamal", "Day", 8));
        List<Machine> machines = List.of(new Machine("M1", "Roller", true));
        List<PowerOutage> outages = List.of(new PowerOutage("O1", 4, 6));

        ScheduleDecoder.DecodedSchedule result = decoder.decode(new int[]{0, 1}, tasks, workers, machines, outages);

        assertEquals(2, result.scheduled().size());
        for (ScheduleEntry entry : result.scheduled()) {
            for (int h = entry.startHour(); h < entry.endHour(); h++) {
                assertFalse(h % 24 >= 4 && h % 24 < 6, "Task must not overlap the outage window");
            }
        }
    }

    @Test
    void doesNotDoubleBookAMachine() {
        List<ProductionTask> tasks = List.of(
                new ProductionTask("T1", "Task A", 2, 5),
                new ProductionTask("T2", "Task B", 2, 5)
        );
        List<Worker> workers = List.of(new Worker("W1", "Kamal", "Day", 8), new Worker("W2", "Nimal", "Day", 8));
        List<Machine> machines = List.of(new Machine("M1", "Only Machine", true));

        ScheduleDecoder.DecodedSchedule result = decoder.decode(new int[]{0, 1}, tasks, workers, machines, List.of());

        assertEquals(2, result.scheduled().size());
        ScheduleEntry first = result.scheduled().get(0);
        ScheduleEntry second = result.scheduled().get(1);
        boolean overlap = first.startHour() < second.endHour() && second.startHour() < first.endHour();
        assertFalse(overlap, "The single machine cannot serve two overlapping tasks");
    }

    @Test
    void leavesTaskUnscheduledWhenNoWorkerHasEnoughRemainingHours() {
        List<ProductionTask> tasks = List.of(new ProductionTask("T1", "Long Task", 10, 5));
        List<Worker> workers = List.of(new Worker("W1", "Kamal", "Day", 4));
        List<Machine> machines = List.of(new Machine("M1", "Roller", true));

        ScheduleDecoder.DecodedSchedule result = decoder.decode(new int[]{0}, tasks, workers, machines, List.of());

        assertTrue(result.scheduled().isEmpty());
        assertEquals(1, result.unscheduled().size());
    }

    @Test
    void unavailableMachineIsNeverUsed() {
        List<ProductionTask> tasks = List.of(new ProductionTask("T1", "Task A", 2, 5));
        List<Worker> workers = List.of(new Worker("W1", "Kamal", "Day", 8));
        List<Machine> machines = List.of(new Machine("M1", "Broken", false));

        ScheduleDecoder.DecodedSchedule result = decoder.decode(new int[]{0}, tasks, workers, machines, List.of());

        assertTrue(result.scheduled().isEmpty());
    }
}
