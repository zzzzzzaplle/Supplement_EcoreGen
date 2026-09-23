/**
 * Central constant repository for all map-rendering characters.
 */
public final class PipePatterns {

    private PipePatterns() {
        // non-instantiable
    }

    public static final char WALL = '\u2588';

    public static final class Filled {
        public static final char UP_ARROW = '\u25B2';
        public static final char DOWN_ARROW = '\u25BC';
        public static final char LEFT_ARROW = '\u25C0';
        public static final char RIGHT_ARROW = '\u25B6';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u2554';
        public static final char TOP_RIGHT = '\u2557';
        public static final char BOTTOM_LEFT = '\u255A';
        public static final char BOTTOM_RIGHT = '\u255D';
        public static final char CROSS = '\u256C';

        private Filled() {
            // non-instantiable
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '\u25B3';
        public static final char DOWN_ARROW = '\u25BD';
        public static final char LEFT_ARROW = '\u25C1';
        public static final char RIGHT_ARROW = '\u25B7';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u250C';
        public static final char TOP_RIGHT = '\u2510';
        public static final char BOTTOM_LEFT = '\u2514';
        public static final char BOTTOM_RIGHT = '\u2518';
        public static final char CROSS = '\u253C';

        private Unfilled() {
            // non-instantiable
        }
    }
}
