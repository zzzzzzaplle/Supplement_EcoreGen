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
    }

    public void setFilled() {
        isFilled = true;
    }

    public char toSingleChar() {
        if (pointingTo == null) {
            return '?';
        }
        if (isFilled) {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Filled.SOURCE_UP;
                case DOWN:
                    return PipePatterns.Filled.SOURCE_DOWN;
                case LEFT:
                    return PipePatterns.Filled.SOURCE_LEFT;
                case RIGHT:
                    return PipePatterns.Filled.SOURCE_RIGHT;
                default:
                    return '?';
            }
        }
        switch (pointingTo) {
            case UP:
                return PipePatterns.Unfilled.TOP_LEFT;
            case DOWN:
                return PipePatterns.Unfilled.BOTTOM_LEFT;
            case LEFT:
                return PipePatterns.Unfilled.TOP_RIGHT;
            case RIGHT:
                return PipePatterns.Unfilled.BOTTOM_RIGHT;
            default:
                return '?';
        }
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
