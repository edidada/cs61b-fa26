import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
/** Codex supplementary assertions; official classes remain unchanged. */
class IntListSupplementaryTest {
    @Test void incrementPreservesEveryNodeAndHandlesNull() {
        IntList tail = new IntList(-2, null);
        IntList head = new IntList(4, tail);
        assertSame(head, IntList.incrRecursiveDestructive(head, 3));
        assertSame(tail, head.rest);
        assertNull(tail.rest);
        assertEquals(7, head.first);
        assertEquals(1, tail.first);
        assertNull(IntList.incrRecursiveDestructive(null, 5));
    }
    @Test void optionalMethodsPreserveHeadAndTailStructure() {
        IntList tail = new IntList(2, null);
        IntList head = new IntList(1, tail);
        head.addFirst(0);
        assertEquals(0, head.first);
        assertEquals(1, head.rest.first);
        assertSame(tail, head.rest.rest);
        head.addLast(3);
        assertEquals(4, head.size());
        assertEquals(4, head.iterativeSize());
        assertEquals(3, head.get(3));
        assertEquals(6, head.sum());
    }
    @Test void mysteryAnswersMatchSeededGeneratorInInsertionOrder() {
        Random generator = new Random(829);
        int[] first = new int[5];
        int middle = 0;
        for (int index = 0; index < 1000; index++) {
            int value = generator.nextInt(0, 800);
            if (index < 5) first[index] = value;
            if (index == 499) middle = value;
        }
        assertArrayEquals(first, IntListMystery.firstFiveNumbers());
        assertEquals(middle, IntListMystery.middleNumber());
    }
}
