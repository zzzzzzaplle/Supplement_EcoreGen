/**
 * Lightweight string helper utilities.
 */
public final class StringUtils {

    private StringUtils() {
        // non-instantiable
    }

    /**
     * Creates a padding string of the specified character repeated count times.
     *
     * @param count number of repetitions
     * @param ch    character to repeat
     * @return string of ch repeated count times
     */
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
