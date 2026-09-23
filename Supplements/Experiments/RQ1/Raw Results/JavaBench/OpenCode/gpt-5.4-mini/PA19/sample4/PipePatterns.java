/**
 * Central constant repository for all map-rendering characters.
 */
public final class PipePatterns {

    private PipePatterns() {
    }

    public static final char WALL = '\u2588';

    public static final class Filled {
        public static final char UP_ARROW = '\u25B3';
        public static final char DOWN_ARROW = '\u25BD';
        public static final char LEFT_ARROW = '\u25C1';
        public static final char RIGHT_ARROW = '\u25B7';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u255D';
        public static final char TOP_RIGHT = '\u255A';
        public static final char BOTTOM_LEFT = '\u2557';
        public static final char BOTTOM_RIGHT = '\u2554';
        public static final char CROSS = '\u256C';

        private Filled() {
        }
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '\u25B2';
        public static final char DOWN_ARROW = '\u25BC';
        public static final char LEFT_ARROW = '\u25C0';
        public static final char RIGHT_ARROW = '\u25B6';
        public static final char HORIZONTAL = '\u2550';
        public static final char VERTICAL = '\u2551';
        public static final char TOP_LEFT = '\u255D';
        public static final char TOP_RIGHT = '\u255A';
        public static final char BOTTOM_LEFT = '\u2557';
        public static final char BOTTOM_RIGHT = '\u2554';
        public static final char CROSS = '\u256C';

        private Unfilled() {
        }
    }
}
