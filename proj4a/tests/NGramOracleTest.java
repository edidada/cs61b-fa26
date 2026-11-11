import browser.NgordnetQuery;
import main.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import static org.junit.jupiter.api.Assertions.*;

public class NGramOracleTest {
    @TempDir Path directory;

    @Test
    void sparseArithmeticAndCopies() {
        Random random = new Random(61042026);
        for (int trial = 0; trial < 100; trial++) {
            TimeSeries a = new TimeSeries();
            TimeSeries b = new TimeSeries();
            for (int year = 1900; year < 2000; year++) {
                if (random.nextBoolean()) { a.put(year, random.nextDouble()); }
                if (random.nextBoolean()) { b.put(year, 1 + random.nextDouble()); }
            }
            TreeMap<Integer, Double> oldA = new TreeMap<>(a);
            TreeMap<Integer, Double> oldB = new TreeMap<>(b);
            TimeSeries sum = a.plus(b);
            for (int year = 1900; year < 2000; year++) {
                if (a.containsKey(year) || b.containsKey(year)) {
                    assertEquals(a.getOrDefault(year, 0.0) + b.getOrDefault(year, 0.0), sum.get(year));
                } else { assertFalse(sum.containsKey(year)); }
            }
            TimeSeries quotient = a.dividedBy(sum);
            a.forEach((year, value) -> assertEquals(value / sum.get(year), quotient.get(year)));
            assertEquals(oldA, a);
            assertEquals(oldB, b);
            assertEquals(new ArrayList<>(a.keySet()), a.years());
            assertEquals(new ArrayList<>(a.values()), a.data());
            TimeSeries range = new TimeSeries(a, 1920, 1950);
            assertEquals(a.subMap(1920, true, 1950, true), range);
            range.clear(); a.years().clear(); a.data().clear();
            assertEquals(oldA, a);
            assertTrue(new TimeSeries(a, 2000, 1900).isEmpty());
        }
        TimeSeries a = new TimeSeries(); a.put(2000, 2.0);
        assertThrows(IllegalArgumentException.class, () -> a.dividedBy(new TimeSeries()));
    }

    @Test
    void independentSparseFileOracleAndDefensiveQueries() throws Exception {
        Random random = new Random(4102026);
        StringBuilder words = new StringBuilder();
        StringBuilder totals = new StringBuilder();
        double[][] raw = new double[10][40];
        for (int y = 0; y < 40; y++) {
            totals.append(1900 + y).append(',').append(10000 + y).append(",1,1\n");
            for (int w = 0; w < 10; w++) {
                if (random.nextBoolean()) {
                    raw[w][y] = 1 + random.nextInt(1000);
                    words.append("word").append(w).append('\t').append(1900 + y)
                            .append('\t').append((int) raw[w][y]).append("\t1\n");
                }
            }
        }
        Path wf = directory.resolve("words.csv"); Path yf = directory.resolve("years.csv");
        Files.writeString(wf, words); Files.writeString(yf, totals);
        NGramMap map = new NGramMap(wf.toString(), yf.toString());
        for (int w = 0; w < 10; w++) {
            for (int start = 1900; start < 1940; start++) {
                int end = Math.min(1939, start + 7);
                TimeSeries count = map.countHistory("word" + w, start, end);
                TimeSeries weight = map.weightHistory("word" + w, start, end);
                for (int year = start; year <= end; year++) {
                    if (raw[w][year - 1900] != 0) {
                        assertEquals(raw[w][year - 1900], count.get(year));
                        assertEquals(raw[w][year - 1900] / (10000 + year - 1900), weight.get(year), 1e-12);
                    } else { assertFalse(count.containsKey(year)); assertFalse(weight.containsKey(year)); }
                }
                count.clear(); weight.clear();
            }
        }
        TimeSeries sum = map.summedWeightHistory(List.of("word0", "word1", "missing"));
        for (int y = 0; y < 40; y++) {
            double expected = (raw[0][y] + raw[1][y]) / (10000 + y);
            if (expected != 0) { assertEquals(expected, sum.get(1900 + y), 1e-12); }
            else { assertFalse(sum.containsKey(1900 + y)); }
        }
        map.totalCountHistory().clear();
        assertEquals(40, map.totalCountHistory().size());
        TimeSeries old = map.countHistory("word0"); map.countHistory("word0").clear();
        assertEquals(old, map.countHistory("word0"));
        assertTrue(map.countHistory("missing").isEmpty());
        assertTrue(map.weightHistory("missing").isEmpty());
        assertTrue(map.summedWeightHistory(List.of()).isEmpty());
        assertTrue(map.countHistory("word0", 2000, 1900).isEmpty());
    }

    @Test
    void originalCommentedHistoryExampleAndEmptyText() {
        NGramMap map = new NGramMap(Main.WORD_HISTORY_SIZE3_FILE, Main.YEAR_HISTORY_FILE);
        HistoryTextHandler handler = new HistoryTextHandler(map);
        assertEquals("request: {2006=2.44740192927834E-5, 2007=2.464488338318067E-5}\nairport: {2007=6.2068176510855946E-6}\n",
                handler.handle(new NgordnetQuery(List.of("request", "airport"), 2006, 2007, 0)));
        assertEquals("missing: {}\n", handler.handle(new NgordnetQuery(List.of("missing"), 1900, 2020, 0)));
        assertEquals("", handler.handle(new NgordnetQuery(List.of(), 1900, 2020, 0)));
    }

    @Test
    void realDatasetChartAndMissingWords() throws Exception {
        long start = System.nanoTime();
        NGramMap map = new NGramMap(Main.WORD_HISTORY_SIZE14377_FILE, Main.YEAR_HISTORY_FILE);
        double elapsed = (System.nanoTime() - start) / 1e9;
        assertTrue(elapsed < 60, "Official 14377-word load took " + elapsed + " seconds");
        Path evidence = Path.of("evidence"); Files.createDirectories(evidence);
        Files.writeString(evidence.resolve("load-seconds.txt"), Double.toString(elapsed));
        HistoryHandler handler = new HistoryHandler(map);
        for (List<String> words : List.of(List.of("cat", "dog"), List.of("missing-no-such-word"), List.of("cat", "cat"))) {
            byte[] bytes = Base64.getDecoder().decode(handler.handle(new NgordnetQuery(words, 1900, 1950, 0)));
            var image = ImageIO.read(new ByteArrayInputStream(bytes));
            assertNotNull(image); assertEquals(800, image.getWidth()); assertEquals(600, image.getHeight());
            Files.write(evidence.resolve(words.get(0) + ".png"), bytes);
        }
    }
}
