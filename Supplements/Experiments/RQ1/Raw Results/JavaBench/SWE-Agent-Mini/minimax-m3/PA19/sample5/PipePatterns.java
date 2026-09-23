/**
 * Central constant repository for all map-rendering characters.
 */
public final class PipePatterns {

    private PipePatterns() {
    }

    public static final char WALL = '#';

    public static final class Filled {
        private Filled() {
        }

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
    }

    public static final class Unfilled {
        private Unfilled() {
        }

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
    }
}
