public class PipePatterns {
    public static final char WALL = 'W';

    public static class Filled {
        public static final char HORIZONTAL = '━';
        public static final char VERTICAL = '┃';
        public static final char TOP_LEFT = '┗';
        public static final char TOP_RIGHT = '┛';
        public static final char BOTTOM_LEFT = '┏';
        public static final char BOTTOM_RIGHT = '┓';
        public static final char CROSS = '╋';
        public static final char UP = '^';
        public static final char DOWN = 'v';
        public static final char LEFT = '<';
        public static final char RIGHT = '>';

        public static char arrowFor(Direction direction) {
            switch (direction) {
                case UP:
                    return UP;
                case DOWN:
                    return DOWN;
                case LEFT:
                    return LEFT;
                case RIGHT:
                    return RIGHT;
                default:
                    return '?';
            }
        }
    }

    public static class Unfilled {
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = 'L';
        public static final char TOP_RIGHT = 'J';
        public static final char BOTTOM_LEFT = 'F';
        public static final char BOTTOM_RIGHT = '7';
        public static final char CROSS = '+';
        public static final char UP = '^';
        public static final char DOWN = 'v';
        public static final char LEFT = '<';
        public static final char RIGHT = '>';

        public static char arrowFor(Direction direction) {
            switch (direction) {
                case UP:
                    return UP;
                case DOWN:
                    return DOWN;
                case LEFT:
                    return LEFT;
                case RIGHT:
                    return RIGHT;
                default:
                    return '?';
            }
        }
    }

    public PipePatterns() {
    }
}
