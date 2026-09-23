public final class PipePatterns {

    public static final char WALL = '\u2588';

    public static class Filled {
        public static final char UP_ARROW = '\u02c4';
        public static final char DOWN_ARROW = '\u02c5';
        public static final char LEFT_ARROW = '\u02c2';
        public static final char RIGHT_ARROW = '\u02c3';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u2554';
        public static final char TOP_RIGHT = '\u2557';
        public static final char BOTTOM_LEFT = '\u255a';
        public static final char BOTTOM_RIGHT = '\u255d';
        public static final char CROSS = '\u256c';
    }

    public static class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u250c';
        public static final char TOP_RIGHT = '\u2510';
        public static final char BOTTOM_LEFT = '\u2514';
        public static final char BOTTOM_RIGHT = '\u2518';
        public static final char CROSS = '\u253c';
    }

    private PipePatterns() {
    }
}
