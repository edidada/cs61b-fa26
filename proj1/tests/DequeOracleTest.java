import java.util.LinkedList;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DequeOracleTest {
    @Test void randomOperationsMatchJdkDeque() {
        LinkedListDeque61B<Integer> actual = new LinkedListDeque61B<>();
        LinkedList<Integer> expected = new LinkedList<>();
        Random random = new Random(61);
        for (int step = 0; step < 5000; step++) {
            int value = random.nextInt();
            switch (random.nextInt(4)) {
                case 0 -> { actual.addFirst(value); expected.addFirst(value); }
                case 1 -> { actual.addLast(value); expected.addLast(value); }
                case 2 -> assertEquals(expected.pollFirst(), actual.removeFirst());
                default -> assertEquals(expected.pollLast(), actual.removeLast());
            }
            assertEquals(expected, actual.toList());
            assertEquals(expected.size(), actual.size());
            assertEquals(expected.isEmpty(), actual.isEmpty());
            assertEquals(expected.peekFirst(), actual.getFirst());
            assertEquals(expected.peekLast(), actual.getLast());
            assertNull(actual.get(-1)); assertNull(actual.getRecursive(actual.size()));
            if (!expected.isEmpty()) {
                int i = random.nextInt(expected.size());
                assertEquals(expected.get(i), actual.get(i));
                assertEquals(expected.get(i), actual.getRecursive(i));
            }
        }
    }
    @Test void emptyReuseAndIndependentListSnapshot() {
        LinkedListDeque61B<String> deque = new LinkedListDeque61B<>();
        assertNull(deque.removeFirst()); assertNull(deque.removeLast());
        deque.addLast("a"); var snapshot = deque.toList();
        assertEquals("a", deque.removeFirst());
        deque.addFirst("b"); assertEquals("b", deque.removeLast());
        assertEquals(java.util.List.of("a"), snapshot);
        assertTrue(deque.isEmpty());
    }
    @Test void longSequencePreservesOrder() {
        LinkedListDeque61B<Integer> deque = new LinkedListDeque61B<>();
        for (int i = 0; i < 50000; i++) { deque.addLast(i); }
        for (int i = 0; i < 50000; i++) { assertEquals(i, deque.removeFirst()); }
        assertTrue(deque.isEmpty());
    }
}
