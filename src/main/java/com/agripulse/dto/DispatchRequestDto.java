package com.agripulse.dto;

import com.agripulse.model.HarvestBatch;
import com.agripulse.model.RoadEdge;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispatchRequestDto {

    @NotNull(message = "truckCurrentNode is required")
    private String truckCurrentNode;

    private List<HarvestBatch> readyBatches;

    private Map<String, List<RoadEdge>> roadGraph;

    public String getTruckCurrentNode() {
        return truckCurrentNode;
    }

    public void setTruckCurrentNode(String truckCurrentNode) {
        this.truckCurrentNode = truckCurrentNode;
    }

    public List<HarvestBatch> getReadyBatches() {
        return readyBatches;
    }

    public void setReadyBatches(List<HarvestBatch> readyBatches) {
        this.readyBatches = readyBatches;
    }

    public Map<String, List<RoadEdge>> getRoadGraph() {
        return roadGraph;
    }

    public void setRoadGraph(Map<String, List<RoadEdge>> roadGraph) {
        this.roadGraph = roadGraph;
    }
}
