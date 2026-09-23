/**
 * A termination cell (SOURCE or SINK) with a pointing direction and filled state.
 */
public class TerminationCell extends Cell {

    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public Direction getPointingTo() {
        return pointingTo;
    }

    public void setPointingTo(Direction pointingTo) {
        this.pointingTo = pointingTo;
    }

    public TerminationType getType() {
        return type;
    }

    public void setType(TerminationType type) {
        this.type = type;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        if (pointingTo == null) {
            throw new IllegalStateException("Invalid pointingTo value!");
        }
        switch (pointingTo) {
            case UP:
                return isFilled ? PipePatterns.Filled.UP_ARROW : PipePatterns.Unfilled.UP_ARROW;
            case DOWN:
                return isFilled ? PipePatterns.Filled.DOWN_ARROW : PipePatterns.Unfilled.DOWN_ARROW;
            case LEFT:
                return isFilled ? PipePatterns.Filled.LEFT_ARROW : PipePatterns.Unfilled.LEFT_ARROW;
            case RIGHT:
                return isFilled ? PipePatterns.Filled.RIGHT_ARROW : PipePatterns.Unfilled.RIGHT_ARROW;
            default:
                throw new IllegalStateException("Invalid pointingTo value!");
        }
    }
}
