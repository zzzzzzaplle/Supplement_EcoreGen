/**
 * Central constant repository for all map-rendering characters.
 */
public class PipePatterns {

    public static char WALL = '#';

    public static class Filled {

        public static char UP_ARROW = '\u25B2';
        public static char DOWN_ARROW = '\u25BC';
        public static char LEFT_ARROW = '\u25C0';
        public static char RIGHT_ARROW = '\u25B6';

        public static char HORIZONTAL = '\u2501';
        public static char VERTICAL = '\u2503';
        public static char TOP_LEFT = '\u251B';
        public static char TOP_RIGHT = '\u2517';
        public static char BOTTOM_LEFT = '\u2513';
        public static char BOTTOM_RIGHT = '\u250F';
        public static char CROSS = '\u254B';

        private Filled() {
        }
    }

    public static class Unfilled {

        public static char UP_ARROW = '^';
        public static char DOWN_ARROW = 'v';
        public static char LEFT_ARROW = '<';
        public static char RIGHT_ARROW = '>';

        public static char HORIZONTAL = '\u2500';
        public static char VERTICAL = '\u2502';
        public static char TOP_LEFT = '\u2518';
        public static char TOP_RIGHT = '\u2514';
        public static char BOTTOM_LEFT = '\u2510';
        public static char BOTTOM_RIGHT = '\u250C';
        public static char CROSS = '\u253C';

        private Unfilled() {
        }
    }

    private PipePatterns() {
    }
}
