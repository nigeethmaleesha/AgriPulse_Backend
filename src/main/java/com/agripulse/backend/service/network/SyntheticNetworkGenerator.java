package com.agripulse.backend.service.network;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SyntheticNetworkGenerator {

    public FlowNetwork generate(int nodeCount, int edgeCount, long seed, long minCapacity, long maxCapacity) {
        if (nodeCount < 2) {
            throw new IllegalArgumentException("nodeCount must be at least 2");
        }
        int maxPossibleEdges = nodeCount * (nodeCount - 1) / 2;
        if (edgeCount < nodeCount - 1) {
            throw new IllegalArgumentException("edgeCount must be at least nodeCount - 1 so SOURCE can reach FACTORY");
        }
        if (edgeCount > maxPossibleEdges) {
            throw new IllegalArgumentException("For this acyclic benchmark generator, edgeCount cannot exceed " + maxPossibleEdges);
        }
        if (minCapacity <= 0 || maxCapacity < minCapacity) {
            throw new IllegalArgumentException("Capacity range is invalid");
        }

        List<String> nodes = new ArrayList<>();
        nodes.add("SOURCE");
        for (int i = 1; i < nodeCount - 1; i++) {
            nodes.add("N" + i);
        }
        nodes.add("FACTORY");

        Random random = new Random(seed);
        Set<Long> usedPairs = new LinkedHashSet<>();
        List<FlowEdge> edges = new ArrayList<>();

        // Guaranteed source-to-factory chain.
        for (int i = 0; i < nodeCount - 1; i++) {
            addEdge(nodes, edges, usedPairs, i, i + 1, randomCapacity(random, minCapacity, maxCapacity));
        }

        while (edges.size() < edgeCount) {
            int from = random.nextInt(nodeCount - 1);
            int to = from + 1 + random.nextInt(nodeCount - from - 1);
            long key = pairKey(from, to);
            if (usedPairs.contains(key)) {
                continue;
            }
            addEdge(nodes, edges, usedPairs, from, to, randomCapacity(random, minCapacity, maxCapacity));
        }

        return new FlowNetwork(nodes, edges);
    }

    private void addEdge(List<String> nodes, List<FlowEdge> edges, Set<Long> usedPairs,
                         int from, int to, long capacity) {
        usedPairs.add(pairKey(from, to));
        edges.add(new FlowEdge(nodes.get(from), nodes.get(to), capacity));
    }

    private long pairKey(int from, int to) {
        return (((long) from) << 32) ^ (to & 0xffffffffL);
    }

    private long randomCapacity(Random random, long min, long max) {
        if (min == max) return min;
        return min + (long) Math.floor(random.nextDouble() * (max - min + 1));
    }
}
