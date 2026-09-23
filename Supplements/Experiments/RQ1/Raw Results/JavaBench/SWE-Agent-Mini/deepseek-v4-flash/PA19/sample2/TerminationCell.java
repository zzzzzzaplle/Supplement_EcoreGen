/**
 * A cell representing a source or sink termination point.
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

    /**
     * Marks this termination cell as filled.
     */
    public void setFilled() {
        this.isFilled = true;
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

    /**
     * Returns the arrow character based on filling state and pointingTo direction.
     *
     * @return the arrow character
     * @throws IllegalStateException if pointingTo is invalid
     */
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
