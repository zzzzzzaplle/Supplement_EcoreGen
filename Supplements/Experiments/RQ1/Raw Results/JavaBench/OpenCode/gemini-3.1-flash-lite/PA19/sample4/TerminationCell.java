public class TerminationCell extends Cell {
    private boolean isFilled;
    private Direction pointingTo;
    private TerminationType type;

    public TerminationCell() {}

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        this.coord = coord;
        this.type = type;
        this.pointingTo = pointingTo;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public Direction getPointingTo() { return pointingTo; }
    public void setPointingTo(Direction pointingTo) { this.pointingTo = pointingTo; }
    
    public TerminationType getType() { return type; }
    public void setType(TerminationType type) { this.type = type; }
    
    public boolean getIsFilled() { return isFilled; }
    public void setIsFilled(boolean isFilled) { this.isFilled = isFilled; }

    @Override
    public char toSingleChar() {
        // Implementation would use PipePatterns
        return ' '; 
    }
}
