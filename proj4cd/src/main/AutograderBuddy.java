package main;

import browser.NgordnetQueryHandler;

public class AutograderBuddy {
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordHistoryFile, String yearHistoryFile,
            String synsetFile, String hyponymFile) {
        return new HyponymsHandler(new WordNet(synsetFile, hyponymFile),
                () -> new NGramMap(wordHistoryFile, yearHistoryFile));
    }
}
