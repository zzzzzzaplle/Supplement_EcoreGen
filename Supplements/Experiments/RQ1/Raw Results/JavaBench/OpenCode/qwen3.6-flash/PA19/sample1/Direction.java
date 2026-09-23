/**
 * Represents a cardinal direction on the game map.
 */
public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction getOpposite() {
        switch (this) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                throw new IllegalStateException("Invalid direction value!");
        }
    }

    public Coordinate getOffset() {
        switch (this) {
            case UP:
                return new Coordinate(-1, 0);
            case DOWN:
                return new Coordinate(1, 0);
            case LEFT:
                return new Coordinate(0, -1);
            case RIGHT:
                return new Coordinate(0, 1);
            default:
                throw new IllegalStateException("Invalid direction value!");
        }
    }
}
