package com.agripulse.backend.service.scheduling;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactorySchedulingService {

    private final GeneticAlgorithmSchedulerService geneticAlgorithmScheduler;
    private final SimulatedAnnealingSchedulerService simulatedAnnealingScheduler;

    public FactorySchedulingService(GeneticAlgorithmSchedulerService geneticAlgorithmScheduler,
                                     SimulatedAnnealingSchedulerService simulatedAnnealingScheduler) {
        this.geneticAlgorithmScheduler = geneticAlgorithmScheduler;
        this.simulatedAnnealingScheduler = simulatedAnnealingScheduler;
    }

    public ScheduleResult runGenetic(List<ProductionTask> tasks, List<Worker> workers,
                                      List<Machine> machines, List<PowerOutage> outages) {
        return geneticAlgorithmScheduler.schedule(tasks, workers, machines, outages);
    }

    public ScheduleResult runAnnealing(List<ProductionTask> tasks, List<Worker> workers,
                                        List<Machine> machines, List<PowerOutage> outages) {
        return simulatedAnnealingScheduler.schedule(tasks, workers, machines, outages);
    }

    public ScheduleComparisonResult compare(List<ProductionTask> tasks, List<Worker> workers,
                                             List<Machine> machines, List<PowerOutage> outages) {
        ScheduleResult genetic = geneticAlgorithmScheduler.schedule(tasks, workers, machines, outages);
        ScheduleResult annealing = simulatedAnnealingScheduler.schedule(tasks, workers, machines, outages);
        return ScheduleComparisonResult.of(genetic, annealing);
    }
}
