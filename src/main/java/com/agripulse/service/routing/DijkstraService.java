package com.agripulse.service.routing;

import com.agripulse.model.RoadEdge;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraService {

    public static class PathResult {
        private final double totalCost;
        private final List<String> path;

        public PathResult(double totalCost, List<String> path) {
            this.totalCost = totalCost;
            this.path = path;
        }

        public double getTotalCost() {
            return totalCost;
        }

        public List<String> getPath() {
            return path;
        }

        public boolean isReachable() {
            return !Double.isInfinite(totalCost) && path != null && !path.isEmpty();
        }
    }

    private static class NodeDistance implements Comparable<NodeDistance> {
        private final String nodeId;
        private final double distance;

        public NodeDistance(String nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        public String getNodeId() {
            return nodeId;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * Computes the shortest path from startNode to targetNode using Min-Heap Dijkstra's algorithm.
     *
     * @param startNode  Starting truck node
     * @param targetNode Destination collection point node
     * @param roadGraph  Graph adjacency list (nodeId -> list of outgoing edges)
     * @return PathResult containing total route cost and ordered list of visited nodes.
     */
    public PathResult findShortestPath(String startNode, String targetNode, Map<String, List<RoadEdge>> roadGraph) {
        if (startNode == null || targetNode == null) {
            return new PathResult(Double.POSITIVE_INFINITY, Collections.emptyList());
        }

        if (startNode.equals(targetNode)) {
            return new PathResult(0.0, Collections.singletonList(startNode));
        }

        if (roadGraph == null || roadGraph.isEmpty()) {
            return new PathResult(Double.POSITIVE_INFINITY, Collections.emptyList());
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();

        PriorityQueue<NodeDistance> minHeap = new PriorityQueue<>();

        dist.put(startNode, 0.0);
        minHeap.add(new NodeDistance(startNode, 0.0));

        while (!minHeap.isEmpty()) {
            NodeDistance current = minHeap.poll();
            String u = current.getNodeId();
            double currentDist = current.getDistance();

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            if (u.equals(targetNode)) {
                break; // Target node reached with minimal cost
            }

            List<RoadEdge> edges = roadGraph.getOrDefault(u, Collections.emptyList());
            for (RoadEdge edge : edges) {
                if (edge == null || !edge.isOpen()) {
                    continue;
                }

                double edgeCost = edge.getEffectiveCost();
                if (Double.isInfinite(edgeCost) || edgeCost < 0) {
                    continue; // Skip impassable or invalid edges
                }

                String v = edge.getToNode();
                if (v == null || visited.contains(v)) {
                    continue;
                }

                double newDist = currentDist + edgeCost;
                if (newDist < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    dist.put(v, newDist);
                    parent.put(v, u);
                    minHeap.add(new NodeDistance(v, newDist));
                }
            }
        }

        double finalCost = dist.getOrDefault(targetNode, Double.POSITIVE_INFINITY);
        if (Double.isInfinite(finalCost)) {
            return new PathResult(Double.POSITIVE_INFINITY, Collections.emptyList());
        }

        // Path reconstruction from target to start
        LinkedList<String> path = new LinkedList<>();
        String curr = targetNode;
        while (curr != null) {
            path.addFirst(curr);
            curr = parent.get(curr);
        }

        // Ensure path actually starts at startNode
        if (!path.isEmpty() && path.getFirst().equals(startNode)) {
            return new PathResult(finalCost, path);
        } else {
            return new PathResult(Double.POSITIVE_INFINITY, Collections.emptyList());
        }
    }
}
