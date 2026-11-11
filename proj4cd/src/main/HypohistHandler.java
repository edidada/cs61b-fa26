package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;

/** Delegate selected hyponym words to either the history text or image handler. */
public class HypohistHandler extends NgordnetQueryHandler {
    private final HyponymsHandler hyponyms;
    private final NgordnetQueryHandler history;

    public HypohistHandler(HyponymsHandler hyponyms, NgordnetQueryHandler history) {
        this.hyponyms = hyponyms;
        this.history = history;
    }

    @Override
    public String handle(NgordnetQuery query) {
        return history.handle(new NgordnetQuery(hyponyms.select(query),
                query.startYear(), query.endYear(), query.k()));
    }
}
