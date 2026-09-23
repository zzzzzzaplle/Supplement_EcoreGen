public class PipePatterns {

    public static char WALL = '\u2588';

    private PipePatterns() {
    }

    public static class Filled {
        public static char UP_ARROW = '\u2B61';
        public static char DOWN_ARROW = '\u2B63';
        public static char LEFT_ARROW = '\u2B60';
        public static char RIGHT_ARROW = '\u2B62';
        public static char HORIZONTAL = '\u2550';
        public static char VERTICAL = '\u2551';
        public static char TOP_LEFT = '\u2554';
        public static char TOP_RIGHT = '\u2557';
        public static char BOTTOM_LEFT = '\u255A';
        public static char BOTTOM_RIGHT = '\u255D';
        public static char CROSS = '\u256C';

        private Filled() {
        }
    }

    public static class Unfilled {
        public static char UP_ARROW = '\u2191';
        public static char DOWN_ARROW = '\u2193';
        public static char LEFT_ARROW = '\u2190';
        public static char RIGHT_ARROW = '\u2192';
        public static char HORIZONTAL = '\u2500';
        public static char VERTICAL = '\u2502';
        public static char TOP_LEFT = '\u250C';
        public static char TOP_RIGHT = '\u2510';
        public static char BOTTOM_LEFT = '\u2514';
        public static char BOTTOM_RIGHT = '\u2518';
        public static char CROSS = '\u253C';

        private Unfilled() {
        }
    }
}
