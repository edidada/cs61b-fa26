import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/** Two union-find structures prevent bottom-boundary backwash. */
public class Percolation {
    private final int width;
    private final boolean[] open;
    private final WeightedQuickUnionUF paths;
    private final WeightedQuickUnionUF fullness;
    private final int top;
    private final int bottom;
    private int count;

    public Percolation(int n) {
        if (n <= 0 || (long) n * n > Integer.MAX_VALUE - 2) {
            throw new IllegalArgumentException("invalid grid size");
        }
        width = n;
        top = n * n;
        bottom = top + 1;
        open = new boolean[top];
        paths = new WeightedQuickUnionUF(top + 2);
        fullness = new WeightedQuickUnionUF(top + 1);
    }

    private int index(int row, int col) {
        if (row < 0 || row >= width || col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("site outside grid");
        }
        return row * width + col;
    }

    public void open(int row, int col) {
        int site = index(row, col);
        if (open[site]) {
            return;
        }
        open[site] = true;
        count++;
        if (row == 0) {
            paths.union(site, top);
            fullness.union(site, top);
        }
        if (row == width - 1) {
            paths.union(site, bottom);
        }
        connect(site, row - 1, col);
        connect(site, row + 1, col);
        connect(site, row, col - 1);
        connect(site, row, col + 1);
    }

    private void connect(int site, int row, int col) {
        if (row < 0 || row >= width || col < 0 || col >= width) {
            return;
        }
        int neighbor = index(row, col);
        if (open[neighbor]) {
            paths.union(site, neighbor);
            fullness.union(site, neighbor);
        }
    }

    public boolean isOpen(int row, int col) {
        return open[index(row, col)];
    }

    public boolean isFull(int row, int col) {
        int site = index(row, col);
        return open[site] && fullness.connected(site, top);
    }

    public int numberOfOpenSites() {
        return count;
    }

    public boolean percolates() {
        return paths.connected(top, bottom);
    }
}
