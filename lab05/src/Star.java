import edu.princeton.cs.algs4.In;

import java.util.List;

public class Star implements Comparable<Star> {
    private final String name;
    private final double distanceLy;
    private final double massMsun;
    private final double mV;

    public Star(String name, double distanceLy, double massMsun, double mV) {
        this.name = name;
        this.distanceLy = distanceLy;
        this.massMsun = massMsun;
        this.mV = mV;
    }

    @Override
    public String toString() {
        return String.format("Star{name='%s', distanceLy=%.6f, massMsun=%s, mV=%.2f}",
                name, distanceLy, String.format("%.3f", massMsun), mV);
    }

    // You do not need to modify anything above this line
    // (except the "public class Star" line).

    @Override
    public int compareTo(Star other) {
        return Double.compare(massMsun, other.massMsun);
    }

    public static void main(String[] args) {
        In in = new In("data/stars20.txt");
        List<Star> stars = ParseUtils.readCsv(in);

        System.out.println(java.util.Collections.max(stars));
    }
}
