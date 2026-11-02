import org.junit.jupiter.api.Test;
import timing.AList;
import static org.junit.jupiter.api.Assertions.*;
class AListGrowthTest {
    @Test void preservesElementsAcrossRepeatedResizingAndRemoval() {
        AList<Integer> list = new AList<>();
        for (int i = 0; i < 100000; i++) { list.addLast(i); }
        for (int i = 0; i < 100000; i++) { assertEquals(i, list.get(i)); }
        for (int i = 99999; i >= 0; i--) { assertEquals(i, list.removeLast()); }
        assertEquals(0, list.size());
    }
}
