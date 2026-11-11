import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.*;

public class SoundSynthesisTest {
    @Test
    void recurrenceAgainstIndependentArrayOracle() throws Exception {
        for (double frequency : new double[]{100, 261.63, 440, 11025, 22050}) {
            GuitarString string = new GuitarString(frequency);
            int n = (int) Math.round(44100 / frequency);
            Field field = GuitarString.class.getDeclaredField("buffer");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Deque61B<Double> buffer = (Deque61B<Double>) field.get(string);
            assertEquals(n, buffer.size());
            double[] oracle = new double[n];
            for (int i = 0; i < n; i++) {
                buffer.removeFirst();
                oracle[i] = Math.sin(i * .7) * .4;
                buffer.addLast(oracle[i]);
            }
            for (int i = 0; i < 20000; i++) {
                int head = i % n;
                assertEquals(oracle[head], string.sample(), 1e-12);
                oracle[head] = .996 * .5 * (oracle[head] + oracle[(head + 1) % n]);
                string.tic();
            }
            string.pluck();
            assertEquals(n, buffer.size());
            for (double value : buffer) {
                assertTrue(value >= -.5 && value < .5);
            }
        }
        for (double bad : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY, 44100}) {
            assertThrows(IllegalArgumentException.class, () -> new GuitarString(bad));
        }
    }

    @Test
    void keyboardKeysAndOverlappingVoices() throws Exception {
        PentatonicKeyboard keyboard = new PentatonicKeyboard();
        assertEquals(0, keyboard.nextSample());
        keyboard.press('x');
        assertEquals(0, keyboard.nextSample());
        keyboard.press('a');
        keyboard.press('s');
        Field field = PentatonicKeyboard.class.getDeclaredField("strings");
        field.setAccessible(true);
        GuitarString[] strings = (GuitarString[]) field.get(keyboard);
        for (int i = 0; i < 1000; i++) {
            double expected = 0;
            for (GuitarString string : strings) {
                expected += string.sample();
            }
            assertEquals(expected, keyboard.nextSample(), 1e-12);
        }
        for (char key : "dfg".toCharArray()) {
            keyboard.press(key);
        }
        for (GuitarString string : strings) {
            assertTrue(Double.isFinite(string.sample()));
        }
    }

    @Test
    void matrixMouseClockScaleAndWrap() {
        ToneMatrix matrix = new ToneMatrix();
        assertTrue(matrix.mouse(true, .5, .5));
        assertTrue(matrix.isOn(0, 0));
        for (int i = 0; i < 100; i++) {
            assertFalse(matrix.mouse(true, .5, .5));
        }
        matrix.mouse(false, 0, 0);
        assertFalse(matrix.mouse(true, -.1, .5));
        matrix.mouse(false, 0, 0);
        assertFalse(matrix.mouse(true, 16, 16));
        for (int i = 0; i < 6000; i++) {
            assertTrue(Double.isFinite(matrix.nextSample()));
            assertEquals(0, matrix.column());
        }
        matrix.nextSample();
        assertEquals(1, matrix.column());
        for (int i = 6001; i < 96000; i++) {
            matrix.nextSample();
        }
        matrix.nextSample();
        assertEquals(0, matrix.column());
        assertEquals(130.81, ToneMatrix.frequency(0), 1e-10);
        assertEquals(261.62, ToneMatrix.frequency(5), 1e-10);
        assertEquals(1046.48, ToneMatrix.frequency(15), 1e-10);
        for (int row = 1; row < 16; row++) {
            assertTrue(ToneMatrix.frequency(row) > ToneMatrix.frequency(row - 1));
        }
    }

    @Test
    void renderFirstNoteToPcmWave() throws Exception {
        GuitarString string = new GuitarString(440);
        string.pluck();
        byte[] pcm = new byte[50000 * 2];
        double early = 0;
        double late = 0;
        for (int i = 0; i < 50000; i++) {
            double sample = string.sample();
            assertTrue(Math.abs(sample) <= .5);
            if (i < 1000) { early += sample * sample; }
            if (i >= 49000) { late += sample * sample; }
            short value = (short) Math.round(sample * 32767);
            pcm[2 * i] = (byte) value;
            pcm[2 * i + 1] = (byte) (value >>> 8);
            string.tic();
        }
        assertTrue(early > 0);
        assertTrue(late < early);
        Path path = Path.of("audio", "first-note.wav");
        Files.createDirectories(path.getParent());
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        try (AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(pcm), format, 50000)) {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, path.toFile());
        }
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(path.toFile())) {
            assertEquals(50000, stream.getFrameLength());
            assertEquals(44100, stream.getFormat().getSampleRate());
        }
    }
}
