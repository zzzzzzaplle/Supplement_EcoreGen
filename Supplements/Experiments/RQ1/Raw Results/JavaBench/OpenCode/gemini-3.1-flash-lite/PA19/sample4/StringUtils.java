public class StringUtils {
    public static String repeat(char c, int count) {
        if (count < 0) throw new IllegalArgumentException("Negative count");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(c);
        return sb.toString();
    }
    
    public StringUtils() {}
}
