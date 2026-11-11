package main;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/** A sparse year-to-value series with nonmutating arithmetic. */
public class TimeSeries extends TreeMap<Integer, Double> {
    public static final int MIN_YEAR = 1400;
    public static final int MAX_YEAR = 2100;

    public TimeSeries() {
        super();
    }

    public TimeSeries(TimeSeries ts, int startYear, int endYear) {
        super();
        if (startYear <= endYear) {
            putAll(ts.subMap(startYear, true, endYear, true));
        }
    }

    public List<Integer> years() {
        return new ArrayList<>(keySet());
    }

    public List<Double> data() {
        return new ArrayList<>(values());
    }

    public TimeSeries plus(TimeSeries ts) {
        TimeSeries result = new TimeSeries();
        result.putAll(this);
        ts.forEach((year, value) -> result.merge(year, value, Double::sum));
        return result;
    }

    public TimeSeries dividedBy(TimeSeries ts) {
        TimeSeries result = new TimeSeries();
        for (var entry : entrySet()) {
            if (!ts.containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Missing denominator year: " + entry.getKey());
            }
            result.put(entry.getKey(), entry.getValue() / ts.get(entry.getKey()));
        }
        return result;
    }
}
