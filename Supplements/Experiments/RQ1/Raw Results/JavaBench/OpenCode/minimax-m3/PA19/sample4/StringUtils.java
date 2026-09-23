public class StringUtils {

    public StringUtils() {
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
