public class PipePatterns {
    public static class Filled {
        public static char WALL = 'W';
        public static char HORIZONTAL = '-';
        public static char VERTICAL = '|';
        public static char TOP_LEFT = '1';
        public static char TOP_RIGHT = '2';
        public static char BOTTOM_LEFT = '3';
        public static char BOTTOM_RIGHT = '4';
        public static char CROSS = '+';
    }

    public static class Unfilled {
        public static char WALL = 'W';
        public static char HORIZONTAL = '-';
        public static char VERTICAL = '|';
        public static char TOP_LEFT = '1';
        public static char TOP_RIGHT = '2';
        public static char BOTTOM_LEFT = '3';
        public static char BOTTOM_RIGHT = '4';
        public static char CROSS = '+';
    }

    public static char WALL = 'W';

    public PipePatterns() {
    }

    public char getWALL() {
        return WALL;
    }

    public void setWALL(char wall) {
        WALL = wall;
    }
}
