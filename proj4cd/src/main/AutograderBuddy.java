package main;

import browser.NgordnetQueryHandler;

public class AutograderBuddy {
    /** For k=0 the NGrams paths are deliberately unused. */
    public static NgordnetQueryHandler getHyponymsHandler(
            String wordHistoryFile, String yearHistoryFile,
            String synsetFile, String hyponymFile) {
        return new HyponymsHandler(new WordNet(synsetFile, hyponymFile));
    }
}
