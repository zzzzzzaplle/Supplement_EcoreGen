public class PipePatterns {

    public static final char WALL = '#';

    public static class Filled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '=';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '+';
        public static final char TOP_RIGHT = '+';
        public static final char BOTTOM_LEFT = '+';
        public static final char BOTTOM_RIGHT = '+';
        public static final char CROSS = '+';

        public Filled() {
        }
    }

    public static class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '+';
        public static final char TOP_RIGHT = '+';
        public static final char BOTTOM_LEFT = '+';
        public static final char BOTTOM_RIGHT = '+';
        public static final char CROSS = '+';

        public Unfilled() {
        }
    }

    public PipePatterns() {
    }
}
