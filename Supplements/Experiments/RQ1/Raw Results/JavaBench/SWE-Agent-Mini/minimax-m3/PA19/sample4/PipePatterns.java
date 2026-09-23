/**
 * Central constant repository for all map-rendering characters.
 */
public class PipePatterns {

    public static final char WALL = '\u2588';

    private PipePatterns() {
    }

    public static class Filled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';

        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u2518';
        public static final char TOP_RIGHT = '\u2514';
        public static final char BOTTOM_LEFT = '\u2510';
        public static final char BOTTOM_RIGHT = '\u250C';
        public static final char CROSS = '\u253C';

        private Filled() {
        }
    }

    public static class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';

        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u2518';
        public static final char TOP_RIGHT = '\u2514';
        public static final char BOTTOM_LEFT = '\u2510';
        public static final char BOTTOM_RIGHT = '\u250C';
        public static final char CROSS = '\u253C';

        private Unfilled() {
        }
    }
}
