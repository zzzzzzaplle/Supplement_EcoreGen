public class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        this(new Coordinate(0, 0), TerminationType.SOURCE, Direction.UP);
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord;
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

    public void setFilled() {
        this.isFilled = true;
    }

    public char toSingleChar() {
        char arrow;
        if (isFilled) {
            switch (pointingTo) {
                case UP: arrow = PipePatterns.Filled.UP_ARROW; break;
                case DOWN: arrow = PipePatterns.Filled.DOWN_ARROW; break;
                case LEFT: arrow = PipePatterns.Filled.LEFT_ARROW; break;
                case RIGHT: arrow = PipePatterns.Filled.RIGHT_ARROW; break;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP: arrow = PipePatterns.Unfilled.UP_ARROW; break;
                case DOWN: arrow = PipePatterns.Unfilled.DOWN_ARROW; break;
                case LEFT: arrow = PipePatterns.Unfilled.LEFT_ARROW; break;
                case RIGHT: arrow = PipePatterns.Unfilled.RIGHT_ARROW; break;
                default: throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
        return arrow;
    }
}
