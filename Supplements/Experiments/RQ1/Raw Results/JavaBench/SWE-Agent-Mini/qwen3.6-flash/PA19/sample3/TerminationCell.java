import java.util.Optional;

/**
 * A termination cell (SOURCE or SINK) that can be filled with water.
 */
public class TerminationCell extends Cell {
    public TerminationType type;
    public Direction pointingTo;
    private boolean isFilled;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char filledChar;
        char unfilledChar;
        switch (pointingTo) {
            case UP:
                filledChar = PipePatterns.Filled.UP_ARROW;
                unfilledChar = PipePatterns.Unfilled.UP_ARROW;
                break;
            case DOWN:
                filledChar = PipePatterns.Filled.DOWN_ARROW;
                unfilledChar = PipePatterns.Unfilled.DOWN_ARROW;
                break;
            case LEFT:
                filledChar = PipePatterns.Filled.LEFT_ARROW;
                unfilledChar = PipePatterns.Unfilled.LEFT_ARROW;
                break;
            case RIGHT:
                filledChar = PipePatterns.Filled.RIGHT_ARROW;
                unfilledChar = PipePatterns.Unfilled.RIGHT_ARROW;
                break;
            default:
                throw new IllegalStateException("Invalid pointingTo value!");
        }
        return isFilled ? filledChar : unfilledChar;
    }

    public boolean isFilled() {
        return isFilled;
    }
}
