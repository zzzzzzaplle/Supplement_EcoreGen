public class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell() {}
    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public boolean isFilled() { return isFilled; }
    public void setFilled() { this.isFilled = true; }
    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction pointingTo) { this.pointingTo = pointingTo; }
    public TerminationType getType() { return type; }
    public void setType(TerminationType type) { this.type = type; }

    @Override
    public char toSingleChar() {
        if (type == TerminationType.SOURCE) {
            if (isFilled) {
                switch (pointingTo) {
                    case UP: return PipePatterns.Filled.UP;
                    case DOWN: return PipePatterns.Filled.DOWN;
                    case LEFT: return PipePatterns.Filled.LEFT;
                    case RIGHT: return PipePatterns.Filled.RIGHT;
                }
            } else {
                switch (pointingTo) {
                    case UP: return PipePatterns.Unfilled.UP;
                    case DOWN: return PipePatterns.Unfilled.DOWN;
                    case LEFT: return PipePatterns.Unfilled.LEFT;
                    case RIGHT: return PipePatterns.Unfilled.RIGHT;
                }
            }
        } else { // SINK
            if (isFilled) {
                switch (pointingTo) {
                    case UP: return PipePatterns.Filled.UP;
                    case DOWN: return PipePatterns.Filled.DOWN;
                    case LEFT: return PipePatterns.Filled.LEFT;
                    case RIGHT: return PipePatterns.Filled.RIGHT;
                }
            } else {
                switch (pointingTo) {
                    case UP: return PipePatterns.Unfilled.UP;
                    case DOWN: return PipePatterns.Unfilled.DOWN;
                    case LEFT: return PipePatterns.Unfilled.LEFT;
                    case RIGHT: return PipePatterns.Unfilled.RIGHT;
                }
            }
        }
        return '?';
    }
}
