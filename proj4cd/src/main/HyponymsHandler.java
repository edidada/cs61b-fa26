package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/** Intersect meanings first, then rank positive raw counts in the requested interval. */
public class HyponymsHandler extends NgordnetQueryHandler {
    private final WordNet wordNet;
    private final Supplier<NGramMap> mapSource;
    private NGramMap map;

    public HyponymsHandler(WordNet wordNet) {
        this(wordNet, null);
    }

    public HyponymsHandler(WordNet wordNet, Supplier<NGramMap> mapSource) {
        this.wordNet = wordNet;
        this.mapSource = mapSource;
    }

    /** Lazy once-only indexing keeps k=0 independent of unused NGrams paths. */
    private synchronized NGramMap counts() {
        if (map == null) {
            if (mapSource == null) {
                throw new IllegalStateException("Positive k requires NGram data");
            }
            map = mapSource.get();
        }
        return map;
    }

    @Override
    public String handle(NgordnetQuery query) {
        if (query.k() < 0) {
            throw new IllegalArgumentException("k must be nonnegative");
        }
        List<String> candidates = wordNet.hyponyms(query.words());
        if (query.k() == 0 || candidates.isEmpty()) {
            return candidates.toString();
        }
        List<RankedWord> ranked = new ArrayList<>();
        NGramMap counts = counts();
        for (String word : candidates) {
            double total = 0;
            for (double count : counts.countHistory(word, query.startYear(), query.endYear()).values()) {
                total += count;
            }
            if (total > 0) {
                ranked.add(new RankedWord(word, total));
            }
        }
        ranked.sort(Comparator.comparingDouble(RankedWord::count).reversed()
                .thenComparing(RankedWord::word));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(query.k(), ranked.size()); i++) {
            result.add(ranked.get(i).word());
        }
        result.sort(Comparator.naturalOrder());
        return result.toString();
    }

    private record RankedWord(String word, double count) { }
}
