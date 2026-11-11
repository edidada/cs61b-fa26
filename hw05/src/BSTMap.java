import java.util.TreeSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A binary-search-tree based Map61B. Keys compare by Comparable.
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        K key;
        V val;
        Node left;
        Node right;

        Node(K k, V v) {
            key = k;
            val = v;
        }
    }

    private Node root;
    private int size;

    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        root = putHelper(root, key, value);
    }

    private Node putHelper(Node node, K key, V value) {
        if (node == null) {
            size += 1;
            return new Node(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = putHelper(node.left, key, value);
        } else if (cmp > 0) {
            node.right = putHelper(node.right, key, value);
        } else {
            node.val = value;
        }
        return node;
    }

    @Override
    public V get(K key) {
        Node found = getHelper(root, key);
        return found == null ? null : found.val;
    }

    private Node getHelper(Node node, K key) {
        if (node == null || key == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        }
        if (cmp < 0) {
            return getHelper(node.left, key);
        }
        return getHelper(node.right, key);
    }

    @Override
    public boolean containsKey(K key) {
        return getHelper(root, key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new TreeSet<>();
        keySetHelper(root, keys);
        return keys;
    }

    private void keySetHelper(Node node, Set<K> keys) {
        if (node == null) {
            return;
        }
        keySetHelper(node.left, keys);
        keys.add(node.key);
        keySetHelper(node.right, keys);
    }

    @Override
    public V remove(K key) {
        V removed = get(key);
        if (!containsKey(key)) {
            return null;
        }
        root = removeHelper(root, key);
        size -= 1;
        return removed;
    }

    /** Returns the tree with the node matching key removed, destructively. */
    private Node removeHelper(Node node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key);
            return node;
        }
        if (cmp > 0) {
            node.right = removeHelper(node.right, key);
            return node;
        }
        // Found it. Three cases.
        if (node.left == null) {
            return node.right;
        }
        if (node.right == null) {
            return node.left;
        }
        // Two children: replace with the smallest key of the right subtree.
        Node successor = node.right;
        while (successor.left != null) {
            successor = successor.left;
        }
        node.key = successor.key;
        node.val = successor.val;
        node.right = removeHelper(node.right, node.key);
        return node;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}

