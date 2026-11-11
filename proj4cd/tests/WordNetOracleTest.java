import main.*;
import browser.NgordnetQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class WordNetOracleTest {
    @TempDir Path directory;

    @Test
    void iterativeGraphHandlesCyclesDeepChainsAndFreshResults() {
        DirectedGraph graph = new DirectedGraph();
        for (int i = 0; i < 20000; i++) { graph.addEdge(i, i + 1); }
        graph.addEdge(20000, 0); graph.addVertex(-5); graph.addEdge(0, 1);
        Set<Integer> result = graph.reachable(List.of(0, 0, 5000));
        assertEquals(20001, result.size()); result.clear();
        assertEquals(20001, graph.reachable(List.of(0)).size());
        assertEquals(Set.of(-5), graph.reachable(List.of(-5)));
        assertTrue(graph.reachable(List.of(-7)).isEmpty());
    }

    @Test
    void wordsNotNodeIntersectionAndNoCrossSenseJump() throws Exception {
        Path syn = directory.resolve("synsets"); Path hyp = directory.resolve("hyponyms");
        Files.writeString(syn, "90,car,vehicle\n10,bug,insect\n80,beetle,car sense\n20,beetle,insect sense\n30,root,root\n40,bridge shared,first meaning\n50,shared,second meaning\n60,forbidden,not reachable\n70,Zebra apple,case sensitivity\n");
        Files.writeString(hyp, "90,80\n10,20\n30,40\n50,60\n30,70\n30,70\n");
        WordNet map = new WordNet(syn.toString(), hyp.toString());
        assertEquals(List.of("beetle"), map.hyponyms(List.of("car", "bug")));
        assertEquals(List.of("Zebra", "apple", "bridge", "root", "shared"), map.hyponyms("root"));
        assertFalse(map.hyponyms("root").contains("forbidden"));
        assertEquals(List.of("bridge", "forbidden", "shared"), map.hyponyms("shared"));
        assertTrue(map.hyponyms(List.of("car", "missing")).isEmpty());
        assertTrue(map.hyponyms(List.<String>of()).isEmpty());
        assertEquals(map.hyponyms("car"), map.hyponyms(List.of("car", "car", "car")));
        map.hyponyms("car").clear(); assertEquals(List.of("beetle", "car"), map.hyponyms("car"));
        var handler = AutograderBuddy.getHyponymsHandler("does-not-exist", "does-not-exist", syn.toString(), hyp.toString());
        assertEquals("[beetle]", handler.handle(new NgordnetQuery(List.of("car", "bug"), 2100, 1400, 0)));
    }

    @Test
    void randomGraphsAgainstFloydWarshallOracle() throws Exception {
        Random random = new Random(6142026);
        for (int trial = 0; trial < 40; trial++) {
            int n = 20;
            boolean[][] reachable = new boolean[n][n];
            StringBuilder nodes = new StringBuilder(); StringBuilder edges = new StringBuilder();
            for (int i = n - 1; i >= 0; i--) {
                nodes.append(i * 97 + 10).append(",W").append(i).append(" alias").append(i % 5).append(",definition,with,commas\n");
                reachable[i][i] = true;
                for (int j = 0; j < n; j++) {
                    if (random.nextInt(15) == 0) {
                        reachable[i][j] = true;
                        edges.append(i * 97 + 10).append(',').append(j * 97 + 10).append('\n');
                    }
                }
            }
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) { reachable[i][j] |= reachable[i][k] && reachable[k][j]; }
                }
            }
            Path syn = directory.resolve("syn" + trial); Path hyp = directory.resolve("hyp" + trial);
            Files.writeString(syn, nodes); Files.writeString(hyp, edges);
            WordNet map = new WordNet(syn.toString(), hyp.toString());
            for (int w = 0; w < n; w++) {
                TreeSet<String> expected = new TreeSet<>();
                for (int j = 0; j < n; j++) {
                    if (reachable[w][j]) { expected.add("W" + j); expected.add("alias" + j % 5); }
                }
                assertEquals(new ArrayList<>(expected), map.hyponyms("W" + w));
                int other = (w + 7) % n;
                TreeSet<String> expectedOther = new TreeSet<>();
                for (int j = 0; j < n; j++) {
                    if (reachable[other][j]) { expectedOther.add("W" + j); expectedOther.add("alias" + j % 5); }
                }
                expected.retainAll(expectedOther);
                assertEquals(new ArrayList<>(expected), map.hyponyms(List.of("W" + w, "W" + other)));
            }
            TreeSet<String> alias = new TreeSet<>();
            for (int i = 0; i < n; i += 5) {
                for (int j = 0; j < n; j++) {
                    if (reachable[i][j]) { alias.add("W" + j); alias.add("alias" + j % 5); }
                }
            }
            assertEquals(new ArrayList<>(alias), map.hyponyms("alias0"));
        }
    }

    @Test
    void officialFullDatasetExamplesAndParsedOnce() throws Exception {
        long start = System.nanoTime();
        WordNet map = new WordNet(Main.SYNSETS_SIZE82191_FILE, Main.HYPONYMS_SIZE82191_FILE);
        double seconds = (System.nanoTime() - start) / 1e9;
        assertEquals(List.of("video", "video_recording", "videocassette", "videotape"), map.hyponyms(List.of("video", "recording")));
        assertEquals(List.of("apple_tart", "lobster_tart", "quiche", "quiche_Lorraine", "tart", "tartlet"), map.hyponyms(List.of("pastry", "tart")));
        Files.createDirectories(Path.of("evidence"));
        Files.writeString(Path.of("evidence/load-seconds.txt"), Double.toString(seconds));
        Path syn = directory.resolve("once-syn"); Path hyp = directory.resolve("once-hyp");
        Files.writeString(syn, "0,root,root\n1,leaf,leaf\n"); Files.writeString(hyp, "0,1\n");
        WordNet once = new WordNet(syn.toString(), hyp.toString());
        Files.delete(syn); Files.delete(hyp);
        assertEquals(List.of("leaf", "root"), once.hyponyms("root"));
    }
}
