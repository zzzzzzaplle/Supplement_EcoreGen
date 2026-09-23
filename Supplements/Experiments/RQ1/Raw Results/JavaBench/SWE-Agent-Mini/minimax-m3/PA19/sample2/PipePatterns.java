/**
 * Central constant repository for all map-rendering characters.
 */
public final class PipePatterns {

    private PipePatterns() {
    }

    public static final char WALL = '\u2588';

    public static final class Filled {
        private Filled() {
        }

        public static final char UP_ARROW = '\u25B2';
        public static final char DOWN_ARROW = '\u25BC';
        public static final char LEFT_ARROW = '\u25C0';
        public static final char RIGHT_ARROW = '\u25B6';

        public static final char HORIZONTAL = '\u2501';
        public static final char VERTICAL = '\u2503';
        public static final char TOP_LEFT = '\u2517';
        public static final char TOP_RIGHT = '\u251B';
        public static final char BOTTOM_LEFT = '\u2513';
        public static final char BOTTOM_RIGHT = '\u250F';
        public static final char CROSS = '\u254B';
    }

    public static final class Unfilled {
        private Unfilled() {
        }

        public static final char UP_ARROW = '\u25B3';
        public static final char DOWN_ARROW = '\u25BD';
        public static final char LEFT_ARROW = '\u25C1';
        public static final char RIGHT_ARROW = '\u25B7';

        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u2518';
        public static final char TOP_RIGHT = '\u2514';
        public static final char BOTTOM_LEFT = '\u2510';
        public static final char BOTTOM_RIGHT = '\u250C';
        public static final char CROSS = '\u253C';
    }
}
