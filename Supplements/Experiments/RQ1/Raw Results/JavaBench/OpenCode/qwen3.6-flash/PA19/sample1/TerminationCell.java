/**
 * A termination cell (SOURCE or SINK) on the map border.
 */
public class TerminationCell extends Cell {

    public  Direction pointingTo;
    public  TerminationType type;
    private  boolean isFilled;

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

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char arrow;
        switch (pointingTo) {
            case UP:
                arrow = isFilled ? PipePatterns.Filled.UP_ARROW : PipePatterns.Unfilled.UP_ARROW;
                break;
            case DOWN:
                arrow = isFilled ? PipePatterns.Filled.DOWN_ARROW : PipePatterns.Unfilled.DOWN_ARROW;
                break;
            case LEFT:
                arrow = isFilled ? PipePatterns.Filled.LEFT_ARROW : PipePatterns.Unfilled.LEFT_ARROW;
                break;
            case RIGHT:
                arrow = isFilled ? PipePatterns.Filled.RIGHT_ARROW : PipePatterns.Unfilled.RIGHT_ARROW;
                break;
            default:
                throw new IllegalStateException("Invalid pointingTo value!");
        }
        return arrow;
    }
}
