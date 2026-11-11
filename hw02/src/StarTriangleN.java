public class StarTriangleN {
    /**
     * Prints a right-aligned triangle of stars ('*') with N lines.
     * The first row contains 1 star, the second 2 stars, and so on.
     */
    public static void starTriangle(int N) {
        for (int row = 1; row <= N; row++) {
            System.out.println(" ".repeat(N - row) + "*".repeat(row));
        }
    }

    void main() {
        starTriangle(7);
    }
}
