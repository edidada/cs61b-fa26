import java.util.ArrayList;
import java.util.List;

/** A circular doubly linked deque with a permanent sentinel. */
public class LinkedListDeque61B<T> implements Deque61B<T> {
    private static class Node<E> {
        E item;
        Node<E> previous;
        Node<E> next;
        Node(E item, Node<E> previous, Node<E> next) {
            this.item = item; this.previous = previous; this.next = next;
        }
    }
    private final Node<T> sentinel = new Node<>(null, null, null);
    private int size;

    public LinkedListDeque61B() { sentinel.next = sentinel; sentinel.previous = sentinel; }
    private void insert(T item, Node<T> previous, Node<T> next) {
        Node<T> node = new Node<>(item, previous, next);
        previous.next = node; next.previous = node; size++;
    }
    public void addFirst(T item) { insert(item, sentinel, sentinel.next); }
    public void addLast(T item) { insert(item, sentinel.previous, sentinel); }
    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public T getFirst() { return isEmpty() ? null : sentinel.next.item; }
    public T getLast() { return isEmpty() ? null : sentinel.previous.item; }
    private T remove(Node<T> node) {
        if (node == sentinel) { return null; }
        node.previous.next = node.next; node.next.previous = node.previous; size--;
        return node.item;
    }
    public T removeFirst() { return remove(sentinel.next); }
    public T removeLast() { return remove(sentinel.previous); }
    public T get(int index) {
        if (index < 0 || index >= size) { return null; }
        Node<T> node;
        if (index < size / 2) {
            node = sentinel.next;
            for (int i = 0; i < index; i++) { node = node.next; }
        } else {
            node = sentinel.previous;
            for (int i = size - 1; i > index; i--) { node = node.previous; }
        }
        return node.item;
    }
    private T getRecursive(Node<T> node, int index) {
        return index == 0 ? node.item : getRecursive(node.next, index - 1);
    }
    public T getRecursive(int index) {
        if (index < 0 || index >= size) { return null; }
        return getRecursive(sentinel.next, index);
    }
    public List<T> toList() {
        List<T> copy = new ArrayList<>(size);
        for (Node<T> node = sentinel.next; node != sentinel; node = node.next) { copy.add(node.item); }
        return copy;
    }
}
