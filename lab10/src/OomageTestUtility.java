import java.util.List;
public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int m) {
        if (m <= 0) { throw new IllegalArgumentException("Bucket count must be positive"); }
        int[] counts = new int[m];
        for (Oomage oomage : oomages) { counts[Math.floorMod(oomage.hashCode(), m)]++; }
        for (int count : counts) {
            if (count < oomages.size() / 50.0 || count > oomages.size() / 2.5) { return false; }
        }
        return true;
    }
}
