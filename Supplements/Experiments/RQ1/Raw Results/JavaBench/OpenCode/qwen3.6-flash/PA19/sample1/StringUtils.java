/**
 * Provide lightweight string helper utilities. Class design: utility class with a private
 * constructor to prevent instantiation.
 */
public final class StringUtils {

    private StringUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
