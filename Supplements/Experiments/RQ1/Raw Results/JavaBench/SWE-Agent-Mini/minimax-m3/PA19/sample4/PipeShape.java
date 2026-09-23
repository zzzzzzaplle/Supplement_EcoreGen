/**
 * Pipe shape definitions.
 */
public enum PipeShape {
    HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
    VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
    TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
    TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
    BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
    BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
    CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

    private char filledChar;
    private char unfilledChar;

    PipeShape(char filledChar, char unfilledChar) {
        this.filledChar = filledChar;
        this.unfilledChar = unfilledChar;
    }

    public char getCharByState(boolean isFilled) {
        return isFilled ? filledChar : unfilledChar;
    }
}
