public class PipePatterns {
    public static char WALL = 'W';
    public static class Filled {
        public static char HORIZONTAL = '-';
        public static char VERTICAL = '|';
        public static char TOP_LEFT = '1';
        public static char TOP_RIGHT = '2';
        public static char BOTTOM_LEFT = '3';
        public static char BOTTOM_RIGHT = '4';
        public static char CROSS = '+';
    }
    public static class Unfilled {
        public static char HORIZONTAL = '=';
        public static char VERTICAL = '!';
        public static char TOP_LEFT = 'a';
        public static char TOP_RIGHT = 'b';
        public static char BOTTOM_LEFT = 'c';
        public static char BOTTOM_RIGHT = 'd';
        public static char CROSS = 'x';
    }
}
