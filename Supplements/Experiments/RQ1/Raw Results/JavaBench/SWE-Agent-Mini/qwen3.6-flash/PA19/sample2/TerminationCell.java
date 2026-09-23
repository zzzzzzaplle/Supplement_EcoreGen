class TerminationCell extends Cell {

    private boolean isFilled;
    public Direction pointingTo;
    public TerminationType type;

    public TerminationCell(Coordinate coord, TerminationType type, Direction pointingTo) {
        super.coord = coord;
        this.type = type;
        this.pointingTo = pointingTo;
        this.isFilled = false;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        char arrow;
        if (isFilled) {
            switch (pointingTo) {
                case UP:
                    arrow = PipePatterns.Filled.UP_ARROW;
                    break;
                case DOWN:
                    arrow = PipePatterns.Filled.DOWN_ARROW;
                    break;
                case LEFT:
                    arrow = PipePatterns.Filled.LEFT_ARROW;
                    break;
                case RIGHT:
                    arrow = PipePatterns.Filled.RIGHT_ARROW;
                    break;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        } else {
            switch (pointingTo) {
                case UP:
                    arrow = PipePatterns.Unfilled.UP_ARROW;
                    break;
                case DOWN:
                    arrow = PipePatterns.Unfilled.DOWN_ARROW;
                    break;
                case LEFT:
                    arrow = PipePatterns.Unfilled.LEFT_ARROW;
                    break;
                case RIGHT:
                    arrow = PipePatterns.Unfilled.RIGHT_ARROW;
                    break;
                default:
                    throw new IllegalStateException("Invalid pointingTo value!");
            }
        }
        return arrow;
    }
}
