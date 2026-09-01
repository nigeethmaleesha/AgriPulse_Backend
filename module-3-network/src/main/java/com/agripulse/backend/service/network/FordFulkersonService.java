package com.agripulse.backend.service.network;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Member 5 core PDSA implementation.
 *
 * Ford-Fulkerson maximum flow using an explicit residual-capacity matrix and
 * a stack/DFS augmenting-path search. Capacities are integer kg/day values.
 */
@Service
public class FordFulkersonService {

    public MaxFlowComputation computeMaxFlow(FlowNetwork network, String sourceCode, String sinkCode) {
        validateNetwork(network, sourceCode, sinkCode);

        long started = System.nanoTime();

        List<String> nodes = network.nodeCodes();
        int n = nodes.size();
        Map<String, Integer> indexByCode = new LinkedHashMap<>();
        for (int i = 0; i < n; i++) {
            indexByCode.put(normalize(nodes.get(i)), i);
        }

        int source = indexByCode.get(normalize(sourceCode));
        int sink = indexByCode.get(normalize(sinkCode));

        long[][] capacity = new long[n][n];
        for (FlowEdge edge : network.edges()) {
            int u = indexByCode.get(normalize(edge.fromCode()));
            int v = indexByCode.get(normalize(edge.toCode()));
            if (capacity[u][v] != 0) {
                throw new IllegalArgumentException("Duplicate directed edge is not supported: "
                        + edge.fromCode() + " -> " + edge.toCode());
            }
            capacity[u][v] = edge.capacityKgPerDay();
        }

        long[][] residual = copyMatrix(capacity);
        long[][] flow = new long[n][n];
        long maxFlow = 0L;
        List<AugmentingPath> augmentingPaths = new ArrayList<>();

        int[] parent = new int[n];
        while (findAugmentingPathDfs(residual, source, sink, parent)) {
            long pathFlow = Long.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }

            List<String> path = new ArrayList<>();
            for (int v = sink; ; v = parent[v]) {
                path.add(nodes.get(v));
                if (v == source) {
                    break;
                }
            }
            Collections.reverse(path);

            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
            }

            maxFlow = Math.addExact(maxFlow, pathFlow);
            augmentingPaths.add(new AugmentingPath(path, pathFlow));
        }

        List<EdgeFlow> edgeFlows = new ArrayList<>();
        for (FlowEdge edge : network.edges()) {
            int u = indexByCode.get(normalize(edge.fromCode()));
            int v = indexByCode.get(normalize(edge.toCode()));
            long actualFlow = Math.max(0L, flow[u][v]);
            edgeFlows.add(new EdgeFlow(
                    edge.fromCode(),
                    edge.toCode(),
                    edge.capacityKgPerDay(),
                    actualFlow,
                    Math.max(0L, edge.capacityKgPerDay() - actualFlow)
            ));
        }

        long elapsed = System.nanoTime() - started;
        double estimatedMemoryMb = estimateMatrixMemoryMb(n);

        return new MaxFlowComputation(
                sourceCode,
                sinkCode,
                maxFlow,
                edgeFlows,
                augmentingPaths,
                elapsed,
                estimatedMemoryMb
        );
    }

    private boolean findAugmentingPathDfs(long[][] residual, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[residual.length];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);
        visited[source] = true;

        while (!stack.isEmpty()) {
            int u = stack.pop();

            for (int v = 0; v < residual.length; v++) {
                if (!visited[v] && residual[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == sink) {
                        return true;
                    }
                    stack.push(v);
                }
            }
        }
        return false;
    }

    private void validateNetwork(FlowNetwork network, String sourceCode, String sinkCode) {
        if (network == null || network.nodeCodes() == null || network.edges() == null) {
            throw new IllegalArgumentException("Network, node list and edge list are required");
        }
        if (network.nodeCodes().size() < 2) {
            throw new IllegalArgumentException("At least two nodes are required");
        }
        if (sourceCode == null || sourceCode.isBlank() || sinkCode == null || sinkCode.isBlank()) {
            throw new IllegalArgumentException("Source and sink codes are required");
        }
        if (normalize(sourceCode).equals(normalize(sinkCode))) {
            throw new IllegalArgumentException("Source and sink must be different nodes");
        }

        Set<String> uniqueNodes = new HashSet<>();
        for (String code : network.nodeCodes()) {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Node code cannot be blank");
            }
            if (!uniqueNodes.add(normalize(code))) {
                throw new IllegalArgumentException("Duplicate node code: " + code);
            }
        }

        if (!uniqueNodes.contains(normalize(sourceCode))) {
            throw new IllegalArgumentException("Source node not found: " + sourceCode);
        }
        if (!uniqueNodes.contains(normalize(sinkCode))) {
            throw new IllegalArgumentException("Sink node not found: " + sinkCode);
        }

        Set<String> edgePairs = new HashSet<>();
        for (FlowEdge edge : network.edges()) {
            String from = normalize(edge.fromCode());
            String to = normalize(edge.toCode());
            if (!uniqueNodes.contains(from) || !uniqueNodes.contains(to)) {
                throw new IllegalArgumentException("Edge references an unknown node: "
                        + edge.fromCode() + " -> " + edge.toCode());
            }
            if (from.equals(to)) {
                throw new IllegalArgumentException("Self-loop is not allowed: " + edge.fromCode());
            }
            String pair = from + "->" + to;
            if (!edgePairs.add(pair)) {
                throw new IllegalArgumentException("Duplicate directed edge: "
                        + edge.fromCode() + " -> " + edge.toCode());
            }
        }
    }

    private long[][] copyMatrix(long[][] source) {
        long[][] copy = new long[source.length][source.length];
        for (int i = 0; i < source.length; i++) {
            System.arraycopy(source[i], 0, copy[i], 0, source.length);
        }
        return copy;
    }

    private double estimateMatrixMemoryMb(int n) {
        // capacity + residual + flow = 3 long[n][n] matrices.
        // This is a transparent algorithm-structure estimate, not total JVM memory.
        long bytes = 3L * n * n * Long.BYTES;
        return bytes / (1024.0 * 1024.0);
    }

    private String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
