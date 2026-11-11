import java.util.Random;

public class IntListMystery {

    /**
     *  Builds an IntList containing some mystery numbers.
     */
    public static void mystery() {
        Random r = new Random(829);
        IntList L = null;

        int idx = 0;
        int nextInt;
        while (idx < 1000) {
            nextInt = r.nextInt(0, 800);
            L = new IntList(nextInt, L);
            idx += 1;
        }
    }

    /**
     *  Returns the first five numbers added to the mystery IntList
     *  (insertion order, oldest five).
     */
    public static int[] firstFiveNumbers() {
        return new int[]{241, 326, 30, 140, 21};
    }

    /**
     *  Returns the 500th number added to the mystery IntList.
     */
    public static int middleNumber() {
        return 491;
    }

    public static void main(String[] args) {
        mystery();
    }
}
