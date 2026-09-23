public class StringUtils {
    public StringUtils() {
    }

    public static String pad(char ch, int count) {
        return String.valueOf(ch).repeat(count);
    }
}
