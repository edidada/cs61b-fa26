import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;
/** Codex tests based on public specification; not private grader assertions. */
class ArrayDequeOracleTest {
    @Test void emptyAndInvalidIndices() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();
        assertTrue(deque.isEmpty()); assertEquals(0, deque.size());
        assertNull(deque.getFirst()); assertNull(deque.getLast());
        assertNull(deque.removeFirst()); assertNull(deque.removeLast());
        assertNull(deque.get(-1)); assertNull(deque.get(0)); assertNull(deque.get(100));
        assertThrows(UnsupportedOperationException.class, () -> deque.getRecursive(0));
    }
    @Test void wraparoundGrowthShrinkAndReuseBothEnds() {
        for (boolean front : new boolean[]{true, false}) {
            Deque61B<Integer> deque = new ArrayDeque61B<>();
            List<Integer> oracle = new ArrayList<>();
            for (int round = 0; round < 5; round++) {
                for (int i = 0; i < 1000; i++) {
                    if (front) { deque.addFirst(i); oracle.add(0, i); }
                    else { deque.addLast(i); oracle.add(i); }
                }
                assertEquals(oracle, deque.toList());
                while (!oracle.isEmpty()) {
                    assertEquals(oracle.remove(0), deque.removeFirst());
                    if (!oracle.isEmpty()) assertEquals(oracle.remove(oracle.size() - 1), deque.removeLast());
                }
                assertTrue(deque.isEmpty()); assertEquals(0, deque.size());
            }
        }
    }
    @Test void randomMixedOperationsAgainstList() {
        Deque61B<Integer> deque = new ArrayDeque61B<>();
        List<Integer> oracle = new ArrayList<>();
        Random random = new Random(612);
        for (int step = 0; step < 50000; step++) {
            int value = random.nextInt();
            switch (random.nextInt(4)) {
                case 0 -> { deque.addFirst(value); oracle.add(0, value); }
                case 1 -> { deque.addLast(value); oracle.add(value); }
                case 2 -> assertEquals(oracle.isEmpty() ? null : oracle.remove(0), deque.removeFirst());
                default -> assertEquals(oracle.isEmpty() ? null : oracle.remove(oracle.size()-1), deque.removeLast());
            }
            assertEquals(oracle.size(), deque.size());
            assertEquals(oracle.isEmpty(), deque.isEmpty());
            assertEquals(oracle.isEmpty() ? null : oracle.get(0), deque.getFirst());
            assertEquals(oracle.isEmpty() ? null : oracle.get(oracle.size()-1), deque.getLast());
            assertEquals(oracle, deque.toList());
            if (!oracle.isEmpty()) { int index=random.nextInt(oracle.size()); assertEquals(oracle.get(index), deque.get(index)); }
        }
    }
    @Test void copyIteratorEqualityAndString() {
        Deque61B<String> a = new ArrayDeque61B<>(), b = new ArrayDeque61B<>();
        a.addLast("first"); a.addLast("last"); b.addFirst("last"); b.addFirst("first");
        assertEquals(a,b); assertEquals(b,a); assertEquals(a.hashCode(),b.hashCode());
        assertNotEquals(a, "[first, last]"); assertNotEquals(a,null);
        assertEquals("[first, last]",a.toString());
        List<String> copy=a.toList(); copy.clear(); assertEquals(2,a.size());
        Iterator<String> iterator=a.iterator(); assertTrue(iterator.hasNext());
        assertEquals("first",iterator.next()); assertEquals("last",iterator.next());
        assertFalse(iterator.hasNext()); assertThrows(NoSuchElementException.class,iterator::next);
        b.removeLast(); assertNotEquals(a,b); b.addLast("other"); assertNotEquals(a,b);
        List<String> collected=new ArrayList<>(); for(String value:a) collected.add(value);
        assertEquals(List.of("first","last"),collected);
    }
    @Test void removedSlotsReleaseReferencesAndCapacityTracksSize() throws Exception {
        // Implementation-invariant checks kept separate from interface-only coverage tests.
        ArrayDeque61B<Integer> deque=new ArrayDeque61B<>();
        var field=ArrayDeque61B.class.getDeclaredField("items");field.setAccessible(true);
        assertEquals(8,((Object[])field.get(deque)).length);
        for(int i=0;i<10000;i++) deque.addLast(i);
        for(int i=0;i<9999;i++) deque.removeFirst();
        Object[] items=(Object[])field.get(deque); assertTrue(items.length<=16);
        assertEquals(1,java.util.Arrays.stream(items).filter(java.util.Objects::nonNull).count());
        deque.removeLast(); assertTrue(java.util.Arrays.stream((Object[])field.get(deque)).allMatch(java.util.Objects::isNull));
    }
}
