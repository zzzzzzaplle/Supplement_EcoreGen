/**
 * Lightweight string helper utilities.
 */
public final class StringUtils {

    private StringUtils() {
        // prevent instantiation
    }

    /**
     * Creates a string consisting of the given character repeated count times.
     *
     * @param count number of repetitions
     * @param ch    character to repeat
     * @return string of repeated characters
     */
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
