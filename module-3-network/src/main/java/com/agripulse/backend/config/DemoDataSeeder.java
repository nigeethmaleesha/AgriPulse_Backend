package com.agripulse.backend.config;

import com.agripulse.backend.model.CapacityEdge;
import com.agripulse.backend.model.NodeType;
import com.agripulse.backend.model.SupplyNode;
import com.agripulse.backend.repository.CapacityEdgeRepository;
import com.agripulse.backend.repository.SupplyNodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final SupplyNodeRepository nodeRepository;
    private final CapacityEdgeRepository edgeRepository;

    @Value("${agripulse.seed-demo:true}")
    private boolean seedDemo;

    public DemoDataSeeder(SupplyNodeRepository nodeRepository, CapacityEdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDemo || nodeRepository.count() > 0) {
            return;
        }

        Map<String, SupplyNode> n = new LinkedHashMap<>();
        n.put("SOURCE", nodeRepository.save(new SupplyNode("SOURCE", "Daily Tea Supply Source", NodeType.SOURCE, true)));
        n.put("F1", nodeRepository.save(new SupplyNode("F1", "Tea Farm 1", NodeType.FARM, true)));
        n.put("F2", nodeRepository.save(new SupplyNode("F2", "Tea Farm 2", NodeType.FARM, true)));
        n.put("F3", nodeRepository.save(new SupplyNode("F3", "Tea Farm 3", NodeType.FARM, true)));
        n.put("H1", nodeRepository.save(new SupplyNode("H1", "Collection Hub 1", NodeType.HUB, true)));
        n.put("H2", nodeRepository.save(new SupplyNode("H2", "Collection Hub 2", NodeType.HUB, true)));
        n.put("FACTORY", nodeRepository.save(new SupplyNode("FACTORY", "Tea Factory", NodeType.FACTORY, true)));

        saveEdge(n, "SOURCE", "F1", 700, "Daily available harvest from Farm 1");
        saveEdge(n, "SOURCE", "F2", 600, "Daily available harvest from Farm 2");
        saveEdge(n, "SOURCE", "F3", 500, "Daily available harvest from Farm 3");
        saveEdge(n, "F1", "H1", 500, "Farm 1 to Hub 1 handling/road capacity");
        saveEdge(n, "F1", "H2", 200, "Farm 1 to Hub 2 handling/road capacity");
        saveEdge(n, "F2", "H1", 200, "Farm 2 to Hub 1 handling/road capacity");
        saveEdge(n, "F2", "H2", 400, "Farm 2 to Hub 2 handling/road capacity");
        saveEdge(n, "F3", "H2", 400, "Farm 3 to Hub 2 handling/road capacity");
        saveEdge(n, "H1", "FACTORY", 650, "Hub 1 to Factory daily transport capacity");
        saveEdge(n, "H2", "FACTORY", 700, "Hub 2 to Factory daily transport capacity");
    }

    private void saveEdge(Map<String, SupplyNode> nodes, String from, String to, long capacity, String label) {
        edgeRepository.save(new CapacityEdge(nodes.get(from), nodes.get(to), capacity, true, label));
    }
}
