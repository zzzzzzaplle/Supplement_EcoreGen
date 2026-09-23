/**
 * Direction of movement on the game board.
 */
public enum Direction {
    UP(new PositionOffset(-1, 0)),
    DOWN(new PositionOffset(1, 0)),
    LEFT(new PositionOffset(0, -1)),
    RIGHT(new PositionOffset(0, 1));

    private final PositionOffset offset;

    Direction(PositionOffset offset) {
        this.offset = offset;
    }

    /**
     * Gets the PositionOffset for this direction.
     *
     * @return the PositionOffset.
     */
    public PositionOffset getOffset() {
        return offset;
    }

    /**
     * Gets the row offset for this direction.
     *
     * @return the row offset.
     */
    public int getRowOffset() {
        return offset.getDRow();
    }

    /**
     * Gets the column offset for this direction.
     *
     * @return the column offset.
     */
    public int getColOffset() {
        return offset.getDCol();
    }
}
