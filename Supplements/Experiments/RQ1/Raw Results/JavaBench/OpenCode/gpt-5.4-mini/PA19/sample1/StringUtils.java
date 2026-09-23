public class StringUtils {
    public StringUtils() {
    }

    public static String repeat(char ch, int count) {
        if (count == 0) {
            return "";
        }
        return String.valueOf(ch).repeat(count);
    }
}
