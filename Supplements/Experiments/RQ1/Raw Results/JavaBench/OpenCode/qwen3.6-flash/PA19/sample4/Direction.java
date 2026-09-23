public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public Coordinate getOffset() {
        return switch (this) {
            case UP -> new Coordinate(-1, 0);
            case DOWN -> new Coordinate(1, 0);
            case LEFT -> new Coordinate(0, -1);
            case RIGHT -> new Coordinate(0, 1);
        };
    }
}
