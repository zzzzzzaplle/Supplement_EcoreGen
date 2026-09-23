public final class PipePatterns {

    public static final char WALL = '\u2588';

    private PipePatterns() {
    }

    public static final class Filled {
        public static final char UP_ARROW = '\u2191';
        public static final char DOWN_ARROW = '\u2193';
        public static final char LEFT_ARROW = '\u2190';
        public static final char RIGHT_ARROW = '\u2192';

        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u2554';
        public static final char TOP_RIGHT = '\u2557';
        public static final char BOTTOM_LEFT = '\u255A';
        public static final char BOTTOM_RIGHT = '\u255D';
        public static final char CROSS = '\u256C';

        private Filled() {
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '\u02C4';
        public static final char DOWN_ARROW = '\u02C5';
        public static final char LEFT_ARROW = '\u02C2';
        public static final char RIGHT_ARROW = '\u02C3';

        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u250C';
        public static final char TOP_RIGHT = '\u2510';
        public static final char BOTTOM_LEFT = '\u2514';
        public static final char BOTTOM_RIGHT = '\u2518';
        public static final char CROSS = '\u253C';

        private Unfilled() {
        }
    }
}
