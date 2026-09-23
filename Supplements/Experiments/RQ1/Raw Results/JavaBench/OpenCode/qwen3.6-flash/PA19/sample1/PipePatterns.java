/**
 * Central repository for all map-rendering characters. Design: final utility class with
 * private constructor (non-instantiable). Exposed constants: WALL (solid wall block), plus two nested static groups:
 * Filled and Unfilled. Each group defines arrow chars (UP_ARROW, DOWN_ARROW, LEFT_ARROW, RIGHT_ARROW) and pipe-shape
 * chars (HORIZONTAL, VERTICAL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CROSS).
 */
public final class PipePatterns {

    private PipePatterns() {
        throw new IllegalStateException("Utility class");
    }

    public static final char WALL = '\u2588';

    public static final class Filled {
        public static final char UP_ARROW = '\u2191';
        public static final char DOWN_ARROW = '\u2193';
        public static final char LEFT_ARROW = '\u2190';
        public static final char RIGHT_ARROW = '\u2192';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '\u2557';
        public static final char TOP_RIGHT = '\u255E';
        public static final char BOTTOM_LEFT = '\u255D';
        public static final char BOTTOM_RIGHT = '\u255C';
        public static final char CROSS = '+';

        private Filled() {
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = ':';
        public static final char TOP_RIGHT = ';';
        public static final char BOTTOM_LEFT = ',';
        public static final char BOTTOM_RIGHT = '.';
        public static final char CROSS = '*';

        private Unfilled() {
        }
    }
}
