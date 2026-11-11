import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.TreeMap;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
/** Supplementary TreeMap differential oracle; original JUnit4 tests unchanged. */
class BSTMapOracleTest {
    @Test void nullValueMappingCanBeRemoved() {
        BSTMap<Integer, String> map = new BSTMap<>();
        map.put(2, null); map.put(1, "one"); map.put(3, "three");
        assertTrue(map.containsKey(2));
        assertNull(map.remove(2));
        assertFalse(map.containsKey(2));
        assertEquals(2, map.size());
        assertEquals("one", map.get(1));
        assertEquals("three", map.get(3));
    }
    @Test void mixedOperationsMatchTreeMap() {
        BSTMap<Integer, Integer> actual = new BSTMap<>();
        TreeMap<Integer, Integer> expected = new TreeMap<>();
        Random random = new Random(6105);
        for (int step = 0; step < 10000; step++) {
            int key = random.nextInt(200);
            switch (random.nextInt(10)) {
                case 0 -> { actual.clear(); expected.clear(); }
                case 1, 2, 3 -> assertEquals(expected.remove(key), actual.remove(key));
                default -> { Integer value = random.nextBoolean() ? null : random.nextInt(); actual.put(key, value); expected.put(key, value); }
            }
            assertEquals(expected.size(), actual.size());
            assertEquals(expected.keySet(), actual.keySet());
            assertEquals(new ArrayList<>(expected.keySet()), new ArrayList<>(actual.keySet()));
            for (int k = 0; k < 200; k++) {
                assertEquals(expected.containsKey(k), actual.containsKey(k));
                assertEquals(expected.get(k), actual.get(k));
            }
        }
    }
}
