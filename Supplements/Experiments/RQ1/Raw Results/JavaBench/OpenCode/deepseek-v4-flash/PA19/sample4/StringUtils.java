public class StringUtils {

    private StringUtils() {
    }

    public static String createPadding(int count, char ch) {
        return String.valueOf(ch).repeat(count);
    }
}
