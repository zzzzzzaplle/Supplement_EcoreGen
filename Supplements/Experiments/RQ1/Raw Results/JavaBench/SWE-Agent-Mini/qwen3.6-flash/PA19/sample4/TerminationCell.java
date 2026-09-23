import java.util.Optional;

/**
 * A termination cell (SOURCE or SINK) on the map.
 */
public class TerminationCell extends Cell {
    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell() {
    }

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super(coord);
        this.isFilled = false;
        this.pointingTo = pointingTo;
        this.type = type;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setIsFilled(boolean filled) {
        this.isFilled = filled;
    }

    public void setPointingTo(Direction pointingTo) {
        this.pointingTo = pointingTo;
    }

    public Direction getPointingTo() {
        return pointingTo;
    }

    public void setType(TerminationType type) {
        this.type = type;
    }

    public TerminationType getType() {
        return type;
    }

    @Override
    public char toSingleChar() {
        java.util.Map<Direction, char[]> filledArrows = new java.util.HashMap<>();
        filledArrows.put(Direction.UP, new char[]{ PipePatterns.Filled.UP_ARROW });
        filledArrows.put(Direction.DOWN, new char[]{ PipePatterns.Filled.DOWN_ARROW });
        filledArrows.put(Direction.LEFT, new char[]{ PipePatterns.Filled.LEFT_ARROW });
        filledArrows.put(Direction.RIGHT, new char[]{ PipePatterns.Filled.RIGHT_ARROW });

        java.util.Map<Direction, char[]> unfilledArrows = new java.util.HashMap<>();
        unfilledArrows.put(Direction.UP, new char[]{ PipePatterns.Unfilled.UP_ARROW });
        unfilledArrows.put(Direction.DOWN, new char[]{ PipePatterns.Unfilled.DOWN_ARROW });
        unfilledArrows.put(Direction.LEFT, new char[]{ PipePatterns.Unfilled.LEFT_ARROW });
        unfilledArrows.put(Direction.RIGHT, new char[]{ PipePatterns.Unfilled.RIGHT_ARROW });

        if (isFilled) {
            char[] mapped = getArrowForDirection(filledArrows, pointingTo);
            if (mapped != null) {
                return mapped[0];
            }
        } else {
            char[] mapped = getArrowForDirection(unfilledArrows, pointingTo);
            if (mapped != null) {
                return mapped[0];
            }
        }
        throw new IllegalStateException("Invalid pointingTo value!");
    }

    private char[] getArrowForDirection(java.util.Map<Direction, char[]> arrows, Direction dir) {
        return arrows.get(dir);
    }

    @Override
    public Optional<Pipe> getPipe() {
        return Optional.empty();
    }

    @Override
    public void setPipe(Pipe pipe) {
        // Termination cells don't hold pipes
    }
}
