import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

/** Codex supplementary tests derived from starter Javadoc; not official grader tests. */
class Homework02Test {
    private String capture(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream stream = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(stream);
            action.run();
        } finally { System.setOut(original); }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
    @Test void doubleUpExamplesAndEmpty() {
        assertEquals("hheelllloo", DoubleUp.doubleUp("hello"));
        assertEquals("ccaatt", DoubleUp.doubleUp("cat"));
        assertEquals("", DoubleUp.doubleUp(""));
        assertEquals("中中文文", DoubleUp.doubleUp("中文"));
    }
    @Test void indexedExamplesAndMultidigitIndex() {
        assertEquals("h4e3l2l1o0\n", capture(() -> PrintIndexed.printIndexed("hello")));
        assertEquals("c2a1t0\n", capture(() -> PrintIndexed.printIndexed("cat")));
        assertEquals("\n", capture(() -> PrintIndexed.printIndexed("")));
        assertEquals("a10b9c8d7e6f5g4h3i2j1k0\n", capture(() -> PrintIndexed.printIndexed("abcdefghijk")));
    }
    @Test void triangleExactAlignmentAndBoundary() {
        assertEquals("  *\n **\n***\n", capture(() -> StarTriangleN.starTriangle(3)));
        assertEquals("*\n", capture(() -> StarTriangleN.starTriangle(1)));
        assertEquals("", capture(() -> StarTriangleN.starTriangle(0)));
        assertEquals("    *\n   **\n  ***\n ****\n*****\n", capture(StarTriangle5::starTriangle5));
    }
    @Test void everyTriangleRowHasSpecifiedWidthAndStarCount() {
        for (int n = 1; n <= 25; n++) {
            final int size = n;
            String[] rows = capture(() -> StarTriangleN.starTriangle(size)).split("\n");
            assertEquals(n, rows.length);
            for (int row = 0; row < n; row++) {
                assertEquals(n, rows[row].length());
                assertEquals(row + 1, rows[row].chars().filter(c -> c == '*').count());
                assertEquals(" ".repeat(n - row - 1) + "*".repeat(row + 1), rows[row]);
            }
        }
    }
}

