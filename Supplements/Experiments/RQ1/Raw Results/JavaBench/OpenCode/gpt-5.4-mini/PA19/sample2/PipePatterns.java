public class PipePatterns {
    public static final char WALL = 'W';

    public static class Filled {
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '1';
        public static final char TOP_RIGHT = '2';
        public static final char BOTTOM_LEFT = '3';
        public static final char BOTTOM_RIGHT = '4';
        public static final char CROSS = '+';

        public Filled() {
        }
    }

    public static class Unfilled {
        public static final char HORIZONTAL = 'h';
        public static final char VERTICAL = 'v';
        public static final char TOP_LEFT = 'l';
        public static final char TOP_RIGHT = 'r';
        public static final char BOTTOM_LEFT = 'b';
        public static final char BOTTOM_RIGHT = 'n';
        public static final char CROSS = 'x';

        public Unfilled() {
        }
    }

    public PipePatterns() {
    }
}
