/**
 * Lightweight string helper utilities.
 */
public final class StringUtils {

    private StringUtils() {
        // prevent instantiation
    }

    /**
     * Creates a string of the given character repeated count times.
     *
     * @param count number of repetitions
     * @param ch    the character to repeat
     * @return a string of ch repeated count times
     * @throws IllegalArgumentException if count is negative
     */
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
