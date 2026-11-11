/** Karplus-Strong string backed by the Project 2 circular array deque. */
public class GuitarString {
    private static final int SR = 44100;
    private static final double DECAY = .996;
    private final Deque61B<Double> buffer;

    public GuitarString(double frequency) {
        long length = Math.round(SR / frequency);
        if (!Double.isFinite(frequency) || frequency <= 0 || length < 2
                || length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("frequency must yield at least two samples");
        }
        buffer = new ArrayDeque61B<>();
        for (int i = 0; i < length; i++) {
            buffer.addLast(0.0);
        }
    }

    public void pluck() {
        int length = buffer.size();
        for (int i = 0; i < length; i++) {
            buffer.removeFirst();
            buffer.addLast(Math.random() - 0.5);
        }
    }

    public void tic() {
        double first = buffer.removeFirst();
        buffer.addLast(DECAY * (first + buffer.getFirst()) / 2);
    }

    public double sample() {
        return buffer.getFirst();
    }
}
