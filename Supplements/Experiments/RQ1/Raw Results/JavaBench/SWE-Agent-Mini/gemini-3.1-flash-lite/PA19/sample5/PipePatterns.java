public class PipePatterns {
    public static final char WALL = 'W';
    public static class Filled {
        public static final char HORIZONTAL = '-';
        public static final char VERTICAL = '|';
        public static final char TOP_LEFT = '┌';
        public static final char TOP_RIGHT = '┐';
        public static final char BOTTOM_LEFT = '└';
        public static final char BOTTOM_RIGHT = '┘';
        public static final char CROSS = '+';
    }
    public static class Unfilled {
        public static final char HORIZONTAL = '─';
        public static final char VERTICAL = '│';
        public static final char TOP_LEFT = '╭';
        public static final char TOP_RIGHT = '╮';
        public static final char BOTTOM_LEFT = '╰';
        public static final char BOTTOM_RIGHT = '╯';
        public static final char CROSS = '┼';
    }
    public PipePatterns() {}
}
