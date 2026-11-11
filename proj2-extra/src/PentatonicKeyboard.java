import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/** Five independently sustained strings, mixed before advancing their clocks. */
public class PentatonicKeyboard {
    private static final String KEYS = "asdfg";
    private final GuitarString[] strings = new GuitarString[5];

    public PentatonicKeyboard() {
        double[] frequencies = {261.63, 293.66, 329.63, 392.00, 440.00};
        for (int i = 0; i < strings.length; i++) {
            strings[i] = new GuitarString(frequencies[i]);
        }
    }

    public void press(char key) {
        int index = KEYS.indexOf(key);
        if (index >= 0) {
            strings[index].pluck();
        }
    }

    public double nextSample() {
        double sum = 0;
        for (GuitarString string : strings) {
            sum += string.sample();
            string.tic();
        }
        return sum;
    }

    static void main() {
        PentatonicKeyboard keyboard = new PentatonicKeyboard();
        StdDraw.setCanvasSize(400, 100);
        StdDraw.text(0.5, 0.5, "click here, then play: a s d f g");
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                keyboard.press(StdDraw.nextKeyTyped());
            }
            StdAudio.play(keyboard.nextSample());
        }
    }
}
