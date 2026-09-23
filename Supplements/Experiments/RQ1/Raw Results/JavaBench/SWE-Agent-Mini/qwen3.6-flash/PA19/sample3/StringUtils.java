/**
 * Lightweight string helper utilities.
 */
public class StringUtils {

    private StringUtils() {
        // Utility class, non-instantiable
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
