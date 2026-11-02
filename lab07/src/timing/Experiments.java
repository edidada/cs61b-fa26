package timing;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.Random;

public class Experiments {

    private static void printTimingTable(TimingData data) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.println("------------------------------------------------------------");
        for (TimingData.Trial t : data.trials()) {
            System.out.printf("%12d %12.2f %12d %12.2f\n", t.N(), t.seconds(), t.ops(), t.usPerOp());
        }
    }

    public static TimingData exampleListExperiment() {
        TimingData td = new TimingData("ArrayList contains");

        // A single contains call on a small list takes about a microsecond,
        // too little for our stopwatch to see, so each trial times 10000 of them.
        int ops = 10000;

        for (int N = 1000; N <= 512000; N *= 2) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < N; i += 1) {
                list.add(i);
            }
            Random random = new Random(61);

            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < ops; j += 1) {
                list.contains(random.nextInt(N));
            }
            td.add(N, ops, sw.elapsedTime());
        }

        return td;
    }

    public static TimingData exampleFibonacciExperiment() {
        TimingData td = new TimingData("Naive Recursive Fibonacci");

        // One call of fib(N) per trial is enough: for these N, a single call
        // takes milliseconds to a fraction of a second, long enough to measure.
        int ops = 1;

        for (int N = 25; N <= 40; N++) {
            Stopwatch sw = new Stopwatch();
            int fib = FunctionsToBeTimed.fib(N);
            td.add(N, ops, sw.elapsedTime());
        }

        return td;
    }

    private static volatile long sink;

    private static TimingData measure(String name, java.util.function.IntToLongFunction function,
                                      int first, int last) {
        TimingData result = new TimingData(name);
        for (int n = first; n <= last; n *= 2) {
            for (int warm = 0; warm < 3; warm++) { sink = function.applyAsLong(n); }
            Stopwatch watch = new Stopwatch();
            int operations = 0;
            do {
                sink = function.applyAsLong(n);
                operations++;
            } while (watch.elapsedTime() < 0.05 && operations < 100000);
            result.add(n, operations, watch.elapsedTime());
        }
        return result;
    }

    public static TimingData timeSp16fp9p1() {
        return measure("sp16fp9p1", FunctionsToBeTimed::sp16fp9p1, 128, 4096);
    }
    public static TimingData timeSp16fp9p2() {
        return measure("sp16fp9p2", FunctionsToBeTimed::sp16fp9p2, 1024, 262144);
    }
    public static TimingData timeMsf() {
        return measure("msf", FunctionsToBeTimed::msf, 1024, 262144);
    }
    public static TimingData timeSp16fp9p5() {
        return measure("sp16fp9p5", FunctionsToBeTimed::sp16fp9p5, 128, 4096);
    }
    public static TimingData timeAListConstruction() {
        return measure("AList construction", n -> {
            AList<Integer> list = new AList<>();
            for (int i = 0; i < n; i++) { list.addLast(i); }
            return list.getLast() + list.size();
        }, 1000, 1024000);
    }

    public static void main(String[] args) throws java.io.IOException {
        java.nio.file.Path output = java.nio.file.Path.of("results");
        java.nio.file.Files.createDirectories(output);
        TimingData[] experiments = {timeSp16fp9p1(), timeSp16fp9p2(), timeMsf(),
                                   timeSp16fp9p5(), timeAListConstruction()};
        for (int i = 0; i < experiments.length; i++) {
            TimingData data = experiments[i];
            System.out.println(data.name());
            printTimingTable(data);
            java.util.List<String> rows = new java.util.ArrayList<>();
            rows.add("N,seconds,operations,microseconds_per_operation");
            for (TimingData.Trial trial : data.trials()) {
                rows.add(trial.N() + "," + trial.seconds() + "," + trial.ops() + "," + trial.usPerOp());
            }
            java.nio.file.Files.write(output.resolve("experiment-" + i + ".csv"), rows);
            Fit.savePlot(data, output.resolve("experiment-" + i).toString());
        }
    }
}
