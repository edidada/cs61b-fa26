package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

/** Project 4C intentionally ignores dates and k; frequency ranking is Project 4D. */
public class HyponymsHandler extends NgordnetQueryHandler {
    private final WordNet wordNet;

    public HyponymsHandler(WordNet wordNet) {
        this.wordNet = wordNet;
    }

    @Override
    public String handle(NgordnetQuery query) {
        return wordNet.hyponyms(query.words()).toString();
    }
}
