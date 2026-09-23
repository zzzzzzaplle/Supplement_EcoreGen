public class TerminationCell extends Cell implements MapElement {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord;
        this.type = type;
        this.pointingTo = pointingTo;
    }

    public void setFilled() {
        isFilled = true;
    }

    public char toSingleChar() {
        if (pointingTo == null) {
            return '?';
        }
        if (isFilled) {
            return PipePatterns.Filled.arrowFor(pointingTo);
        }
        return PipePatterns.Unfilled.arrowFor(pointingTo);
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
}
