/**
 * Direction enum for grid navigation.
 */
public enum Direction {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int dRow;
    private final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    private static final Direction[] oppositeMap = {DOWN, UP, RIGHT, LEFT};

    public Direction getOpposite() {
        return oppositeMap[this.ordinal()];
    }

    public Coordinate getOffset() {
        return new Coordinate(dRow, dCol);
    }
}
