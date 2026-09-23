public class PipePatterns {
    public static class Filled {
        public static char HORIZONTAL = '=';
        public static char VERTICAL = '|';
        public static char TOP_LEFT = '1';
        public static char TOP_RIGHT = '2';
        public static char BOTTOM_LEFT = '3';
        public static char BOTTOM_RIGHT = '4';
        public static char CROSS = '+';
        public static char SOURCE_UP = '^';
        public static char SOURCE_RIGHT = '>';
        public static char SOURCE_LEFT = '<';
        public static char SOURCE_DOWN = 'v';
        public static char SINK_UP = '^';
        public static char SINK_RIGHT = '>';
        public static char SINK_LEFT = '<';
        public static char SINK_DOWN = 'v';
    }

    public static class Unfilled {
        public static char HORIZONTAL = '-';
        public static char VERTICAL = '|';
        public static char TOP_LEFT = 'L';
        public static char TOP_RIGHT = 'J';
        public static char BOTTOM_LEFT = 'F';
        public static char BOTTOM_RIGHT = '7';
        public static char CROSS = 'X';
    }

    public static char WALL = 'W';

    public PipePatterns() {
    }
}
