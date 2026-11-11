package main;

import browser.NgordnetQuery;
import browser.NgordnetQueryHandler;
import browser.Plotter;
import java.util.ArrayList;
import java.util.List;

public class HistoryHandler extends NgordnetQueryHandler {
    private final NGramMap map;

    public HistoryHandler(NGramMap map) {
        this.map = map;
    }

    @Override
    public String handle(NgordnetQuery query) {
        List<String> labels = new ArrayList<>();
        List<TimeSeries> histories = new ArrayList<>();
        for (String word : query.words()) {
            TimeSeries history = map.weightHistory(word, query.startYear(), query.endYear());
            // XChart requires nonempty, uniquely named series. Missing words remain absent.
            if (!history.isEmpty() && !labels.contains(word)) {
                labels.add(word);
                histories.add(history);
            }
        }
        return Plotter.encodeChartAsString(Plotter.generateTimeSeriesChart(labels, histories));
    }
}
