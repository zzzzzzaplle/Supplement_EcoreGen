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
        // Implementation would use PipePatterns
        return ' ';
    }
}
