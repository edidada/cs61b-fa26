package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import static main.TimeSeries.MAX_YEAR;
import static main.TimeSeries.MIN_YEAR;

/** Index files once, retaining sparse word series and corpus totals. */
public class NGramMap {
    private final Map<String, TimeSeries> histories = new HashMap<>();
    private final TimeSeries totals = new TimeSeries();

    public NGramMap(String wordHistoryFilename, String yearHistoryFilename) {
        try (BufferedReader words = Files.newBufferedReader(Path.of(wordHistoryFilename), StandardCharsets.UTF_8);
             BufferedReader years = Files.newBufferedReader(Path.of(yearHistoryFilename), StandardCharsets.UTF_8)) {
            String line;
            while ((line = words.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] fields = line.split("\\t");
                    histories.computeIfAbsent(fields[0], ignored -> new TimeSeries())
                            .put(Integer.parseInt(fields[1]), Double.parseDouble(fields[2]));
                }
            }
            while ((line = years.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] fields = line.split(",");
                    totals.put(Integer.parseInt(fields[0]), Double.parseDouble(fields[1]));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load NGram data", e);
        }
    }

    public TimeSeries countHistory(String word, int startYear, int endYear) {
        TimeSeries history = histories.get(word);
        return history == null ? new TimeSeries() : new TimeSeries(history, startYear, endYear);
    }

    public TimeSeries countHistory(String word) {
        return countHistory(word, MIN_YEAR, MAX_YEAR);
    }

    public TimeSeries totalCountHistory() {
        return new TimeSeries(totals, MIN_YEAR, MAX_YEAR);
    }

    public TimeSeries weightHistory(String word, int startYear, int endYear) {
        return countHistory(word, startYear, endYear).dividedBy(totals);
    }

    public TimeSeries weightHistory(String word) {
        return weightHistory(word, MIN_YEAR, MAX_YEAR);
    }

    public TimeSeries summedWeightHistory(Collection<String> words, int startYear, int endYear) {
        TimeSeries sum = new TimeSeries();
        for (String word : words) {
            sum = sum.plus(weightHistory(word, startYear, endYear));
        }
        return sum;
    }

    public TimeSeries summedWeightHistory(Collection<String> words) {
        return summedWeightHistory(words, MIN_YEAR, MAX_YEAR);
    }
}
