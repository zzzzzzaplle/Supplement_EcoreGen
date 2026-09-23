/**
 * Provide lightweight string helper utilities.
 */
public final class StringUtils {

    private StringUtils() {
        // prevent instantiation
    }

    /**
     * Creates a padding string of the specified character repeated count times.
     *
     * @param count the number of times to repeat the character
     * @param ch    the character to repeat
     * @return a string of ch repeated count times
     * @throws IllegalArgumentException if count is negative
     */
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
