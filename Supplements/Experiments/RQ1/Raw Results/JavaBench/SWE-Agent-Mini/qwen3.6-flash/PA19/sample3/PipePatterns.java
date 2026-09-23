/**
 * Central constant repository for all map-rendering characters.
 */
public class PipePatterns {

    private PipePatterns() {
        // Utility class, non-instantiable
    }

    public static final char WALL = '#';

    public static class Filled {
        public static final char UP_ARROW = 'U';
        public static final char DOWN_ARROW = 'D';
        public static final char LEFT_ARROW = 'L';
        public static final char RIGHT_ARROW = 'R';

        public static final char HORIZONTAL = 'H';
        public static final char VERTICAL = 'V';
        public static final char TOP_LEFT = 'T';
        public static final char TOP_RIGHT = 'P';
        public static final char BOTTOM_LEFT = 'B';
        public static final char BOTTOM_RIGHT = 'p';
        public static final char CROSS = 'C';
    }

    public static class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';

        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = 'L';
        public static final char TOP_RIGHT = 'J';
        public static final char BOTTOM_LEFT = '7';
        public static final char BOTTOM_RIGHT = 'J';
        public static final char CROSS = '+';
    }
}
