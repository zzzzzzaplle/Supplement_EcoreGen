/**
 * Enum representing movement directions.
 */
public enum Direction {
    UP(0, 1),
    DOWN(0, -1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private PositionOffset offset;
    private int rowOffset;
    private int colOffset;

    Direction(int dRow, int dCol) {
        this.rowOffset = dRow;
        this.colOffset = dCol;
        this.offset = new PositionOffset(dRow, dCol);
    }

    /**
     * Gets the PositionOffset for this direction.
     *
     * @return The PositionOffset.
     */
    public PositionOffset getOffset() {
        return offset;
    }

    /**
     * Gets the row offset for this direction.
     *
     * @return The row offset.
     */
    public int getRowOffset() {
        return rowOffset;
    }

    /**
     * Gets the column offset for this direction.
     *
     * @return The column offset.
     */
    public int getColOffset() {
        return colOffset;
    }
}
