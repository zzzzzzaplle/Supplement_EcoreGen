/**
 * Utility class for pipe rendering character constants.
 */
public final class PipePatterns {

    public static final char WALL = '#';

    public static final class Filled {
        public static final char UP_ARROW = '\u25C4';
        public static final char DOWN_ARROW = '\u25BA';
        public static final char LEFT_ARROW = '\u25C4';
        public static final char RIGHT_ARROW = '\u25BA';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u255E';
        public static final char TOP_RIGHT = '\u255D';
        public static final char BOTTOM_LEFT = '\u2560';
        public static final char BOTTOM_RIGHT = '\u255F';
        public static final char CROSS = '\u253C';

        private Filled() {}
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '\u2191';
        public static final char DOWN_ARROW = '\u2193';
        public static final char LEFT_ARROW = '\u2190';
        public static final char RIGHT_ARROW = '\u2192';
        public static final char HORIZONTAL = '\u2500';
        public static final char VERTICAL = '\u2502';
        public static final char TOP_LEFT = '\u251C';
        public static final char TOP_RIGHT = '\u2524';
        public static final char BOTTOM_LEFT = '\u2534';
        public static final char BOTTOM_RIGHT = '\u2518';
        public static final char CROSS = '\u253C';

        private Unfilled() {}
    }

    private PipePatterns() {}
}
