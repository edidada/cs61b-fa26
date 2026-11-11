package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Parse once; traverse synset IDs before projecting them to words. */
public class WordNet {
    private final DirectedGraph graph = new DirectedGraph();
    private final Map<Integer, List<String>> synsets = new HashMap<>();
    private final Map<String, Set<Integer>> meanings = new HashMap<>();

    public WordNet(String synsetFile, String hyponymFile) {
        try (BufferedReader nodes = Files.newBufferedReader(Path.of(synsetFile));
             BufferedReader relations = Files.newBufferedReader(Path.of(hyponymFile))) {
            String line;
            while ((line = nodes.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",", 3);
                int id = Integer.parseInt(fields[0].trim());
                List<String> words = List.of(fields[1].trim().split("\\s+"));
                synsets.put(id, words);
                graph.addVertex(id);
                for (String word : words) {
                    meanings.computeIfAbsent(word, ignored -> new HashSet<>()).add(id);
                }
            }
            while ((line = relations.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split(",");
                int parent = Integer.parseInt(fields[0].trim());
                for (int i = 1; i < fields.length; i++) {
                    int child = Integer.parseInt(fields[i].trim());
                    if (!synsets.containsKey(parent) || !synsets.containsKey(child)) {
                        throw new IllegalArgumentException("Unknown synset in relation");
                    }
                    graph.addEdge(parent, child);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load WordNet", e);
        }
    }

    public List<String> hyponyms(String word) {
        TreeSet<String> result = new TreeSet<>();
        for (int id : graph.reachable(meanings.getOrDefault(word, Set.of()))) {
            result.addAll(synsets.get(id));
        }
        return new ArrayList<>(result);
    }

    public List<String> hyponyms(Collection<String> words) {
        TreeSet<String> common = new TreeSet<>();
        boolean first = true;
        for (String word : words) {
            List<String> reachable = hyponyms(word);
            if (first) {
                common.addAll(reachable);
                first = false;
            } else {
                common.retainAll(reachable);
            }
            if (common.isEmpty()) {
                break;
            }
        }
        return new ArrayList<>(common);
    }
}
