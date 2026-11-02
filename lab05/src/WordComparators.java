import java.util.Comparator;
import java.util.List;

public class WordComparators {

    /** Returns a comparator that orders strings by the number of lowercase 'x' characters (ascending). */
    public static Comparator<String> getXComparator() {
        return getCharComparator('x');
    }

    /** Returns a comparator that orders strings by the count of the given character (ascending). */
    public static Comparator<String> getCharComparator(char c) {
        return Comparator.comparingLong(word -> word.chars().filter(ch -> ch == c).count());
    }

    /** Returns a comparator that orders strings by the total count of the given characters (ascending). */
    public static Comparator<String> getCharListComparator(List<Character> chars) {
        java.util.Set<Character> selected = new java.util.HashSet<>(chars);
        return Comparator.comparingLong(word -> word.chars().filter(ch -> selected.contains((char) ch)).count());
    }
}
