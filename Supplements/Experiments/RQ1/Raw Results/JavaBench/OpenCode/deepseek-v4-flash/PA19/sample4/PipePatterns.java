public final class PipePatterns {

    private PipePatterns() {
    }

    public static final char WALL = '\u2588';

    public static final class Filled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u255D';
        public static final char TOP_RIGHT = '\u255A';
        public static final char BOTTOM_LEFT = '\u2557';
        public static final char BOTTOM_RIGHT = '\u2554';
        public static final char CROSS = '\u256C';

        private Filled() {
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u255D';
        public static final char TOP_RIGHT = '\u255A';
        public static final char BOTTOM_LEFT = '\u2557';
        public static final char BOTTOM_RIGHT = '\u2554';
        public static final char CROSS = '\u256C';

        private Unfilled() {
        }
    }
}
