public class StringUtils {
    private StringUtils() {}
    public static String repeat(char c, int count) {
        if (count < 0) throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<count; i++) sb.append(c);
        return sb.toString();
    }
}
