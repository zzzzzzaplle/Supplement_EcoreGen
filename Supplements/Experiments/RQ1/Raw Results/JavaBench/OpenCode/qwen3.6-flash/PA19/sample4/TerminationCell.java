public class TerminationCell extends Cell {
    private boolean isFilled;
    public final Direction pointingTo;
    public final TerminationType type;

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.isFilled = false;
        this.type = type;
        this.pointingTo = pointingTo;
    }

    public Direction getPointingTo() {
        return pointingTo;
    }

    public TerminationType getType() {
        return type;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public char toSingleChar() {
        if (isFilled) {
            return switch (pointingTo) {
                case UP -> PipePatterns.Filled.UP_ARROW;
                case DOWN -> PipePatterns.Filled.DOWN_ARROW;
                case LEFT -> PipePatterns.Filled.LEFT_ARROW;
                case RIGHT -> PipePatterns.Filled.RIGHT_ARROW;
            };
        } else {
            return switch (pointingTo) {
                case UP -> PipePatterns.Unfilled.UP_ARROW;
                case DOWN -> PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT -> PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT -> PipePatterns.Unfilled.RIGHT_ARROW;
            };
        }
    }
}
