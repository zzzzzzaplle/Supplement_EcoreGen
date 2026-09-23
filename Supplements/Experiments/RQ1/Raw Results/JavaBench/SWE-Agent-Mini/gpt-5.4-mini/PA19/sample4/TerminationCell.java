public class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord;
        this.type = type;
        this.pointingTo = pointingTo;
    }

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean isFilled) {
        this.isFilled = isFilled;
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
        isFilled = true;
    }

    public char toSingleChar() {
        if (pointingTo == null) {
            return '?';
        }
        switch (pointingTo) {
            case UP:
                return isFilled ? '^' : '^';
            case DOWN:
                return isFilled ? 'v' : 'v';
            case LEFT:
                return isFilled ? '<' : '<';
            case RIGHT:
                return isFilled ? '>' : '>';
            default:
                return '?';
        }
    }
}
