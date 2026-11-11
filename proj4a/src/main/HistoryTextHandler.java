package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

public class HistoryTextHandler extends NgordnetQueryHandler {
    private final NGramMap map;

    public HistoryTextHandler(NGramMap map) {
        this.map = map;
    }

    @Override
    public String handle(NgordnetQuery query) {
        StringBuilder result = new StringBuilder();
        for (String word : query.words()) {
            result.append(word).append(": ")
                    .append(map.weightHistory(word, query.startYear(), query.endYear())).append('\n');
        }
        return result.toString();
    }
}
