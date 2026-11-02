import edu.princeton.cs.algs4.In;

import java.util.Comparator;

public class WordFinder {
    /**
     *  Returns the maximum string according to the provider comparator.
     *  If multiple strings are considered equal by c, return the first in
     *  the array.
     *  Use loops. Don't use Collections.max or similar.
     */
    public static String findMax(String[] strings, Comparator<String> c) {
        if (strings.length == 0) { throw new IllegalArgumentException("No strings"); }
        String maximum = strings[0];
        for (int i = 1; i < strings.length; i++) {
            if (c.compare(strings[i], maximum) > 0) { maximum = strings[i]; }
        }
        return maximum;
    }

    public static void main(String[] args) {
        In in = new In("data/mobydick.txt");
        String[] words = in.readAllStrings();

        // TODO: Print only the word with the most lower case vowels.
        //       Use your findMax method!
        //
        //       Start by creating a Comparator that compares based on lower case vowels.
        Comparator<String> vowelComparator = WordComparators.getCharListComparator(java.util.List.of('a', 'e', 'i', 'o', 'u'));
        System.out.println(findMax(words, vowelComparator));

        // Optional task: Play around with lists of words from Wikipedia articles.
        // String[] zebraWords = ParseUtils.fetchWords("https://en.wikipedia.org/wiki/zebra");
        // System.out.println(findMax(zebraWords, vowelComparator));
    }
}
