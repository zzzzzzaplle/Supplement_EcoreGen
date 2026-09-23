/**
 * A termination cell (source or sink) on the map border.
 */
public class TerminationCell extends Cell {

    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
        super();
        this.isFilled = false;
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.isFilled = false;
        this.type = type;
        this.pointingTo = pointingTo;
    }

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

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }

    @Override
    public char toSingleChar() {
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
