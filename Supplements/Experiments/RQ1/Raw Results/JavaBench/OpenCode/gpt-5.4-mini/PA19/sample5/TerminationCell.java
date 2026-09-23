public class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.type = type;
        this.pointingTo = pointingTo;
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

    public char toSingleChar() {
        if (pointingTo == null) {
            return '?';
        }
        switch (pointingTo) {
            case UP:
                return isFilled ? '^' : '^';
            case RIGHT:
                return isFilled ? '>' : '>';
            case LEFT:
                return isFilled ? '<' : '<';
            case DOWN:
                return isFilled ? 'v' : 'v';
            default:
                return '?';
        }
    }
}
