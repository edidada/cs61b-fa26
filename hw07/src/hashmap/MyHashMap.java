package hashmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/** Separate chaining with interchangeable Collection buckets and geometric growth. */
public class MyHashMap<K, V> implements Map61B<K, V> {
    protected class Node {
        K key;
        V value;
        Node(K k, V v) { key = k; value = v; }
    }

    private Collection<Node>[] buckets;
    private int size;
    private final double maxLoad;

    public MyHashMap() { this(16, .75); }
    public MyHashMap(int initialCapacity) { this(initialCapacity, .75); }
    public MyHashMap(int initialCapacity, double loadFactor) {
        if (initialCapacity < 1 || !Double.isFinite(loadFactor) || loadFactor <= 0) {
            throw new IllegalArgumentException("Positive capacity and finite positive load factor required");
        }
        maxLoad = loadFactor;
        buckets = table(initialCapacity);
    }

    protected Collection<Node> createBucket() { return new LinkedList<>(); }

    @SuppressWarnings("unchecked")
    private Collection<Node>[] table(int capacity) {
        Collection<Node>[] result = (Collection<Node>[]) new Collection[capacity];
        for (int i = 0; i < capacity; i++) { result[i] = createBucket(); }
        return result;
    }

    private Node find(K key) {
        for (Node node : buckets[Math.floorMod(key.hashCode(), buckets.length)]) {
            if (node.key.equals(key)) { return node; }
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        Node existing = find(key);
        if (existing != null) { existing.value = value; return; }
        while ((size + 1.0) / buckets.length > maxLoad) { resize(); }
        buckets[Math.floorMod(key.hashCode(), buckets.length)].add(new Node(key, value));
        size++;
    }

    private void resize() {
        Collection<Node>[] old = buckets;
        buckets = table(Math.multiplyExact(old.length, 2));
        for (Collection<Node> bucket : old) {
            for (Node node : bucket) {
                buckets[Math.floorMod(node.key.hashCode(), buckets.length)].add(node);
            }
        }
    }

    @Override
    public V get(K key) { Node node = find(key); return node == null ? null : node.value; }
    @Override
    public boolean containsKey(K key) { return find(key) != null; }
    @Override
    public int size() { return size; }
    @Override
    public void clear() { for (Collection<Node> bucket : buckets) { bucket.clear(); } size = 0; }
    @Override
    public V remove(K key) {
        Iterator<Node> iterator = buckets[Math.floorMod(key.hashCode(), buckets.length)].iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.key.equals(key)) { iterator.remove(); size--; return node.value; }
        }
        return null;
    }
    /** Optional conditional removal; a mapped null still counts as present. */
    public V remove(K key, V value) {
        Node node = find(key);
        if (node == null || !(node.value == value || (node.value != null && node.value.equals(value)))) {
            return null;
        }
        return remove(key);
    }

    private Set<K> keySnapshot() {
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) { keys.add(node.key); }
        }
        return keys;
    }
    @Override
    public Iterator<K> iterator() { return keySnapshot().iterator(); }

    /** Backed view: additions are unsupported; removals and clear update this map. */
    @Override
    public Set<K> keySet() {
        return new Set<>() {
            public int size() { return MyHashMap.this.size(); }
            public boolean isEmpty() { return size() == 0; }
            @SuppressWarnings("unchecked")
            public boolean contains(Object key) { return key != null && MyHashMap.this.containsKey((K) key); }
            public Iterator<K> iterator() {
                Iterator<K> snapshot = keySnapshot().iterator();
                return new Iterator<>() {
                    private K last;
                    private boolean removable;
                    public boolean hasNext() { return snapshot.hasNext(); }
                    public K next() { last = snapshot.next(); removable = true; return last; }
                    public void remove() {
                        if (!removable) { throw new IllegalStateException(); }
                        MyHashMap.this.remove(last); removable = false;
                    }
                };
            }
            public Object[] toArray() { return keySnapshot().toArray(); }
            public <T> T[] toArray(T[] target) { return keySnapshot().toArray(target); }
            public boolean add(K key) { throw new UnsupportedOperationException(); }
            public boolean addAll(Collection<? extends K> keys) { throw new UnsupportedOperationException(); }
            @SuppressWarnings("unchecked")
            public boolean remove(Object key) {
                if (!contains(key)) { return false; }
                MyHashMap.this.remove((K) key); return true;
            }
            public boolean containsAll(Collection<?> keys) {
                for (Object key : keys) { if (!contains(key)) { return false; } }
                return true;
            }
            public boolean retainAll(Collection<?> keys) {
                boolean changed = false;
                for (K key : keySnapshot()) { if (!keys.contains(key)) { remove(key); changed = true; } }
                return changed;
            }
            public boolean removeAll(Collection<?> keys) {
                boolean changed = false;
                for (K key : keySnapshot()) { if (keys.contains(key)) { remove(key); changed = true; } }
                return changed;
            }
            public void clear() { MyHashMap.this.clear(); }
            public boolean equals(Object other) { return keySnapshot().equals(other); }
            public int hashCode() { return keySnapshot().hashCode(); }
            public String toString() { return keySnapshot().toString(); }
        };
    }
}
