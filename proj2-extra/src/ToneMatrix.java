import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/** Sample-clock sequencer; the model can also render without a display or sound card. */
public class ToneMatrix {
    public static final int ROWS = 16;
    public static final int COLS = 16;
    public static final int SAMPLES_PER_COLUMN = 6000;
    private final boolean[][] cells = new boolean[COLS][ROWS];
    private final GuitarString[] strings = new GuitarString[ROWS];
    private int column;
    private int samplesInColumn;
    private boolean mouseDown;

    public ToneMatrix() {
        for (int row = 0; row < ROWS; row++) {
            strings[row] = new GuitarString(frequency(row));
        }
    }

    public static double frequency(int row) {
        if (row < 0 || row >= ROWS) {
            throw new IndexOutOfBoundsException("row");
        }
        int[] offsets = {0, 2, 4, 7, 9};
        return 130.81 * Math.pow(2, (offsets[row % 5] + 12 * (row / 5)) / 12.0);
    }

    public boolean isOn(int col, int row) {
        return cells[col][row];
    }

    public int column() {
        return column;
    }

    /** Only a rising mouse edge toggles a cell; an outside click is ignored. */
    public boolean mouse(boolean pressed, double x, double y) {
        boolean changed = false;
        if (pressed && !mouseDown && x >= 0 && x < COLS && y >= 0 && y < ROWS) {
            int col = (int) x;
            int row = (int) y;
            cells[col][row] = !cells[col][row];
            changed = true;
        }
        mouseDown = pressed;
        return changed;
    }

    public double nextSample() {
        if (samplesInColumn == SAMPLES_PER_COLUMN) {
            column = (column + 1) % COLS;
            samplesInColumn = 0;
        }
        if (samplesInColumn == 0) {
            for (int row = 0; row < ROWS; row++) {
                if (cells[column][row]) {
                    strings[row].pluck();
                }
            }
        }
        double sum = 0;
        for (GuitarString string : strings) {
            sum += string.sample();
            string.tic();
        }
        samplesInColumn++;
        return sum;
    }

    private void draw() {
        StdDraw.clear();
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                StdDraw.setPenColor(cells[col][row] ? StdDraw.BOOK_LIGHT_BLUE : StdDraw.LIGHT_GRAY);
                StdDraw.filledRectangle(col + .5, row + .5, .45, .45);
            }
        }
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.rectangle(column + .5, ROWS / 2.0, .49, ROWS / 2.0 - .01);
        StdDraw.show();
    }

    static void main() {
        ToneMatrix matrix = new ToneMatrix();
        StdDraw.setCanvasSize(640, 640);
        StdDraw.setXscale(0, COLS);
        StdDraw.setYscale(0, ROWS);
        StdDraw.enableDoubleBuffering();
        matrix.draw();
        while (true) {
            boolean changed = matrix.mouse(StdDraw.isMousePressed(), StdDraw.mouseX(), StdDraw.mouseY());
            int previous = matrix.column();
            StdAudio.play(matrix.nextSample());
            if (changed || previous != matrix.column()) {
                matrix.draw();
            }
        }
    }
}
