public class StringUtils {

    public StringUtils() {
    }

    public static String pad(char c, int count) {
        return String.valueOf(c).repeat(count);
    }
}
