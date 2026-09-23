public class TerminationCell extends Cell {
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
        switch (pointingTo) {
            case UP: return '^';
            case DOWN: return 'v';
            case LEFT: return '<';
            case RIGHT: return '>';
            default: return '?';
        }
    }

    public boolean getFilled() { return isFilled; }
    public void setFilled(boolean filled) { isFilled = filled; }
    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction pointingTo) { this.pointingTo = pointingTo; }
    public TerminationType getType() { return type; }
    public void setType(TerminationType type) { this.type = type; }
}
