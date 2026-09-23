/**
 * Direction enum for the pipe game.
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    /**
     * Returns the opposite direction.
     */
    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }

    /**
     * Returns the unit coordinate offset for this direction.
     */
    public Coordinate getOffset() {
        switch (this) {
            case UP: return new Coordinate(-1, 0);
            case DOWN: return new Coordinate(1, 0);
            case LEFT: return new Coordinate(0, -1);
            case RIGHT: return new Coordinate(0, 1);
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }
}
