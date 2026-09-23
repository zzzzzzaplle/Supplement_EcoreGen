/**
 * A termination cell (source or sink) on the map.
 */
public class TerminationCell extends Cell {

    private final Direction pointingTo;
    private final TerminationType type;
    private boolean isFilled;

    public TerminationCell() {
        this.pointingTo = null;
        this.type = null;
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

    public TerminationType getType() {
        return type;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled() {
        this.isFilled = true;
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
