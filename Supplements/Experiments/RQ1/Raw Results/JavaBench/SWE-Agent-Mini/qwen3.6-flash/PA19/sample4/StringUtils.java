/**
 * Lightweight string helper utilities.
 * Non-instantiable utility class.
 */
public final class StringUtils {

    private StringUtils() {
        // non-instantiable
    }

    /**
     * Creates a string of {@code count} repeated {@code ch} characters.
     */
    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
