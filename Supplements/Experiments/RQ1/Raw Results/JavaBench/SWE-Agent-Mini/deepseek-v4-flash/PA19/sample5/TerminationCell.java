/**
 * A cell representing a source or sink termination point.
 */
public class TerminationCell extends Cell {

    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        super();
        this.isFilled = false;
        this.pointingTo = null;
        this.type = null;
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.isFilled = false;
        this.pointingTo = pointingTo;
        this.type = type;
    }

    /**
     * Marks this termination cell as filled.
     */
    public void setFilled() {
        this.isFilled = true;
    }

    public boolean isFilled() {
        return isFilled;
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

    @Override
    public char toSingleChar() {
        if (isFilled) {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Filled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Filled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Filled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Filled.RIGHT_ARROW;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Unfilled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Unfilled.RIGHT_ARROW;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
    }
}
