/**
 * Lightweight string helper utilities.
 */
public class StringUtils {

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }

    private StringUtils() {
    }
}
