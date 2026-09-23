public enum Direction {
    UP(new Coordinate(-1, 0)),
    DOWN(new Coordinate(1, 0)),
    LEFT(new Coordinate(0, -1)),
    RIGHT(new Coordinate(0, 1));

    private Coordinate offset;

    Direction(Coordinate offset) {
        this.offset = offset;
    }

    

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return null;
        }
    }

    public Coordinate getOffset() {
        return offset;
    }

    public Coordinate getOffsetValue() {
        return offset;
    }

    public void setOffset(Coordinate offset) {
        this.offset = offset;
    }
}
