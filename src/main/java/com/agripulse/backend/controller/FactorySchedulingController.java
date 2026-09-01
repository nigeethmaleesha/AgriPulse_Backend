package com.agripulse.backend.controller;

import com.agripulse.backend.service.scheduling.FactorySchedulingService;
import com.agripulse.backend.service.scheduling.Machine;
import com.agripulse.backend.service.scheduling.PowerOutage;
import com.agripulse.backend.service.scheduling.ProductionTask;
import com.agripulse.backend.service.scheduling.ScheduleEntry;
import com.agripulse.backend.service.scheduling.Worker;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduling")
public class FactorySchedulingController {

    private final FactorySchedulingService schedulingService;

    public FactorySchedulingController(
            FactorySchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @PostMapping("/generate")
    public List<ScheduleEntry> generateSchedule(
            @RequestBody SchedulingRequest request) {

        return schedulingService.generateSchedule(
                request.getTasks(),
                request.getWorkers(),
                request.getMachines(),
                request.getOutages()
        );
    }

    public static class SchedulingRequest {

        private List<ProductionTask> tasks;
        private List<Worker> workers;
        private List<Machine> machines;
        private List<PowerOutage> outages;

        public SchedulingRequest() {
        }

        public List<ProductionTask> getTasks() {
            return tasks;
        }

        public void setTasks(List<ProductionTask> tasks) {
            this.tasks = tasks;
        }

        public List<Worker> getWorkers() {
            return workers;
        }

        public void setWorkers(List<Worker> workers) {
            this.workers = workers;
        }

        public List<Machine> getMachines() {
            return machines;
        }

        public void setMachines(List<Machine> machines) {
            this.machines = machines;
        }

        public List<PowerOutage> getOutages() {
            return outages;
        }

        public void setOutages(List<PowerOutage> outages) {
            this.outages = outages;
        }
    }
}