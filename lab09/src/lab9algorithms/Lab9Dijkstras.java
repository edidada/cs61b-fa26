package lab9algorithms;

import lab9lib.ExtrinsicMinPQ;
import lab9lib.Lab9Graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Computes shortest paths from a single source node of a Lab9Graph using
 *  Dijkstra's algorithm, treating each edge weight as a distance.
 *
 *  All of the work happens in the constructor, which builds the shortest
 *  paths tree and records it in the instance variables below. The three
 *  query methods then just read off the answers. ExtrinsicMinPQ (provided)
 *  is the priority queue you'll want for the fringe: it supports
 *  changePriority, which Dijkstra's relies on. */
public class Lab9Dijkstras {
    private final String source;
    /** The best known distance from source to each node reached so far. */
    private final Map<String, Integer> distTo = new HashMap<>();
    /** The node we came from on the best known path to each node. */
    private final Map<String, String> edgeTo = new HashMap<>();

    /** Runs Dijkstra's algorithm on g starting from source.
     *  Throws IllegalArgumentException if source is not a node of g. */
    public Lab9Dijkstras(Lab9Graph g, String source) {
        if (!g.nodes().contains(source)) {
            throw new IllegalArgumentException("no such node: " + source);
        }
        this.source = source;
        for (String u : g.nodes()) {
            for (String v : g.neighbors(u)) {
                if (g.edgeWeight(u, v) < 0) { throw new IllegalArgumentException("Negative edge weight"); }
            }
        }
        ExtrinsicMinPQ fringe = new ExtrinsicMinPQ();
        java.util.Set<String> settled = new java.util.HashSet<>();
        distTo.put(source, 0);
        fringe.add(source, 0);
        while (fringe.size() > 0) {
            String u = fringe.removeSmallest();
            settled.add(u);
            for (String v : g.neighbors(u)) {
                if (settled.contains(v)) { continue; }
                int candidate = Math.addExact(distTo.get(u), g.edgeWeight(u, v));
                if (!distTo.containsKey(v) || candidate < distTo.get(v)) {
                    distTo.put(v, candidate);
                    edgeTo.put(v, u);
                    if (fringe.contains(v)) { fringe.changePriority(v, candidate); }
                    else { fringe.add(v, candidate); }
                }
            }
        }
    }

    /** Returns true if there is a path from the source to v. The source
     *  always has a path to itself. */
    public boolean hasPathTo(String v) {
        return distTo.containsKey(v);
    }

    /** Returns the total weight of the shortest path from the source to v.
     *  Throws IllegalArgumentException if there is no path to v. */
    public int distTo(String v) {
        if (!hasPathTo(v)) { throw new IllegalArgumentException("Unreachable node: " + v); }
        return distTo.get(v);
    }

    /** Returns the shortest path from the source to v as a list of nodes,
     *  starting with the source and ending with v. The path to the source
     *  itself is the one-element list [source].
     *  Throws IllegalArgumentException if there is no path to v. */
    public List<String> shortestPathTo(String v) {
        if (!hasPathTo(v)) { throw new IllegalArgumentException("Unreachable node: " + v); }
        java.util.LinkedList<String> path = new java.util.LinkedList<>();
        for (String u = v; u != null; u = edgeTo.get(u)) { path.addFirst(u); }
        return new java.util.ArrayList<>(path);
    }
}
