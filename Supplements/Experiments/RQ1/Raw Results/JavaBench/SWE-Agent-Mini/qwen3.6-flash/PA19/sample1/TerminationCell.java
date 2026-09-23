/**
 * A termination cell (SOURCE or SINK) with a direction it points to.
 */
public class TerminationCell extends Cell {

    private boolean isFilled;

    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        super();
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
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

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char[] filledArrows = {
            PipePatterns.Filled.UP_ARROW,
            PipePatterns.Filled.DOWN_ARROW,
            PipePatterns.Filled.LEFT_ARROW,
            PipePatterns.Filled.RIGHT_ARROW
        };
        char[] unfilledArrows = {
            PipePatterns.Unfilled.UP_ARROW,
            PipePatterns.Unfilled.DOWN_ARROW,
            PipePatterns.Unfilled.LEFT_ARROW,
            PipePatterns.Unfilled.RIGHT_ARROW
        };

        Direction dir = this.pointingTo;
        int idx = dir.ordinal();
        if (idx >= 0 && idx < 4) {
            return isFilled ? filledArrows[idx] : unfilledArrows[idx];
        }
        throw new IllegalStateException("Invalid pointingTo value!");
    }
}
