/**
 * Enum representing the four cardinal directions on the board.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Returns the PositionOffset corresponding to this direction's movement.
     */
    public PositionOffset getOffset() {
        switch (this) {
            case UP:
                return new PositionOffset(-1, 0);
            case DOWN:
                return new PositionOffset(1, 0);
            case LEFT:
                return new PositionOffset(0, -1);
            case RIGHT:
                return new PositionOffset(0, 1);
            default:
                throw new IllegalStateException("Unknown direction: " + this);
        }
    }

    /**
     * Returns the row offset for this direction.
     */
    public int getRowOffset() {
        switch (this) {
            case UP:
                return -1;
            case DOWN:
                return 1;
            case LEFT:
                return 0;
            case RIGHT:
                return 0;
            default:
                throw new IllegalStateException("Unknown direction: " + this);
        }
    }

    /**
     * Returns the column offset for this direction.
     */
    public int getColOffset() {
        switch (this) {
            case UP:
                return 0;
            case DOWN:
                return 0;
            case LEFT:
                return -1;
            case RIGHT:
                return 1;
            default:
                throw new IllegalStateException("Unknown direction: " + this);
        }
    }
}
