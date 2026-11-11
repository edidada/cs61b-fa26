import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/** Circular array deque. Built-in collections are used only for toList. */
public class ArrayDeque61B<T> implements Deque61B<T> {
    private T[] items;
    private int head;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayDeque61B() { items = (T[]) new Object[8]; }

    private int slot(int index) { return (head + index) % items.length; }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        T[] replacement = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) replacement[i] = items[slot(i)];
        items = replacement;
        head = 0;
    }
    private void grow() { if (size == items.length) resize(items.length * 2); }
    private void shrink() {
        if (items.length >= 16 && size <= items.length / 4) resize(items.length / 2);
    }
    @Override public void addFirst(T x) {
        grow();
        head = (head + items.length - 1) % items.length;
        items[head] = x;
        size++;
    }
    @Override public void addLast(T x) {
        grow();
        items[slot(size)] = x;
        size++;
    }
    @Override public T get(int index) { return index < 0 || index >= size ? null : items[slot(index)]; }
    @Override public T getFirst() { return get(0); }
    @Override public T getLast() { return get(size - 1); }
    @Override public T getRecursive(int index) { throw new UnsupportedOperationException(); }
    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public T removeFirst() {
        if (isEmpty()) return null;
        shrink();
        T value = items[head];
        items[head] = null;
        head = (head + 1) % items.length;
        size--;
        return value;
    }
    @Override public T removeLast() {
        if (isEmpty()) return null;
        shrink();
        int last = slot(size - 1);
        T value = items[last];
        items[last] = null;
        size--;
        return value;
    }
    @Override public List<T> toList() {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) result.add(get(i));
        return result;
    }
    @Override public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index;
            @Override public boolean hasNext() { return index < size; }
            @Override public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return get(index++);
            }
        };
    }
    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Deque61B<?> deque) || size != deque.size()) return false;
        for (int i = 0; i < size; i++) if (!Objects.equals(get(i), deque.get(i))) return false;
        return true;
    }
    @Override public int hashCode() {
        int result = 1;
        for (T value : this) result = 31 * result + Objects.hashCode(value);
        return result;
    }
    @Override public String toString() {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i != 0) result.append(", ");
            result.append(get(i));
        }
        return result.append(']').toString();
    }
}
