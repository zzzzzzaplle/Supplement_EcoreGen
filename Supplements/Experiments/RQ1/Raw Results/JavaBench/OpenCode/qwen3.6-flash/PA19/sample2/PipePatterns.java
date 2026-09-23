public final class PipePatterns {

    public static final char WALL = '█';

    public static final class Filled {
        public static final char UP_ARROW = '↑';
        public static final char DOWN_ARROW = '↓';
        public static final char LEFT_ARROW = '←';
        public static final char RIGHT_ARROW = '→';
        public static final char HORIZONTAL = '═';
        public static final char VERTICAL = '║';
        public static final char TOP_LEFT = '╣';
        public static final char TOP_RIGHT = '╚';
        public static final char BOTTOM_LEFT = '╩';
        public static final char BOTTOM_RIGHT = '╔';
        public static final char CROSS = '╬';
    }

    public static final class Unfilled {
        public static final char UP_ARROW = '^';
        public static final char DOWN_ARROW = 'v';
        public static final char LEFT_ARROW = '<';
        public static final char RIGHT_ARROW = '>';
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '7';
        public static final char TOP_RIGHT = 'L';
        public static final char BOTTOM_LEFT = 'γ';
        public static final char BOTTOM_RIGHT = '¦';
        public static final char CROSS = 'x';
    }

    private PipePatterns() {
    }
}
