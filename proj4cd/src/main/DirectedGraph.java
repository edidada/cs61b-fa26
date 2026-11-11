package main;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Adjacency-list directed graph with iterative multi-source traversal. */
public class DirectedGraph {
    private final Map<Integer, Set<Integer>> edges = new HashMap<>();

    public void addVertex(int id) {
        edges.computeIfAbsent(id, ignored -> new HashSet<>());
    }

    public void addEdge(int from, int to) {
        addVertex(from);
        addVertex(to);
        edges.get(from).add(to);
    }

    public Set<Integer> reachable(Collection<Integer> starts) {
        Set<Integer> visited = new HashSet<>();
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int id : starts) {
            if (edges.containsKey(id) && visited.add(id)) {
                queue.addLast(id);
            }
        }
        while (!queue.isEmpty()) {
            int id = queue.removeFirst();
            for (int child : edges.get(id)) {
                if (visited.add(child)) {
                    queue.addLast(child);
                }
            }
        }
        return visited;
    }
}
