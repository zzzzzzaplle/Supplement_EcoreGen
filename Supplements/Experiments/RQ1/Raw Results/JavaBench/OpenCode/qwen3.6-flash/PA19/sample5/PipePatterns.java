/**
 * Central constant repository for all map-rendering characters.
 */
public final class PipePatterns {

    public static final char WALL = '\u2588';

    public static final class Filled {
        public static final char UP_ARROW = '\u25B2';
        public static final char DOWN_ARROW = '\u25BC';
        public static final char LEFT_ARROW = '\u25C0';
        public static final char RIGHT_ARROW = '\u25B6';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u256E';
        public static final char TOP_RIGHT = '\u256D';
        public static final char BOTTOM_LEFT = '\u256C';
        public static final char BOTTOM_RIGHT = '\u256F';
        public static final char CROSS = '\u254C';
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '/';
        public static final char TOP_RIGHT = '\\';
        public static final char BOTTOM_LEFT = '\\';
        public static final char BOTTOM_RIGHT = '/';
        public static final char CROSS = '+';
    }

    private PipePatterns() {
    }
}
