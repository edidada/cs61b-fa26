import java.util.Arrays;

/** Weighted quick union with full path compression and negative root sizes. */
public class UnionFind {
    private final int[] links;

    public UnionFind(int n) {
        if (n < 0) { throw new IllegalArgumentException("Negative item count"); }
        links = new int[n];
        Arrays.fill(links, -1);
    }

    private void validate(int v) {
        if (v < 0 || v >= links.length) { throw new IllegalArgumentException("Invalid item: " + v); }
    }

    public int sizeOf(int v) { return -links[find(v)]; }

    public int parent(int v) {
        validate(v);
        return links[v];
    }

    public boolean connected(int v1, int v2) { return find(v1) == find(v2); }

    public int find(int v) {
        validate(v);
        int root = v;
        while (links[root] >= 0) { root = links[root]; }
        while (v != root) {
            int next = links[v];
            links[v] = root;
            v = next;
        }
        return root;
    }

    public void union(int v1, int v2) {
        validate(v1);
        validate(v2);
        int a = find(v1), b = find(v2);
        if (a == b) { return; }
        if (links[a] < links[b]) {
            links[a] += links[b];
            links[b] = a;
        } else {
            links[b] += links[a];
            links[a] = b;
        }
    }
}
