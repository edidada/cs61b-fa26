public class PrintIndexed {
    /**
     * Prints each character of a given string followed by the reverse of its index.
     * Example: printIndexed("hello") -> h4e3l2l1o0
     */
    public static void printIndexed(String s) {
        for (int i = 0; i < s.length(); i++) {
            System.out.print(s.charAt(i));
            System.out.print(s.length() - 1 - i);
        }
        System.out.println();
    }

    void main() {
        printIndexed("hello");
        printIndexed("cat"); // should print c2a1t0
    }
}

