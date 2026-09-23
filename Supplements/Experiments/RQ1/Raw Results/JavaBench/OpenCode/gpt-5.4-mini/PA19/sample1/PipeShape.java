public enum PipeShape {
    HORIZONTAL,
    VERTICAL,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    CROSS;

    public char getCharByState(boolean isFilled) {
        return isFilled ? 'P' : 'p';
    }
}
