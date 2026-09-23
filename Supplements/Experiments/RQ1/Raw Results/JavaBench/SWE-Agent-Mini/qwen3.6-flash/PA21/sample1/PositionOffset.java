/**
 * Represents a 2D offset (delta row, delta column).
 */
public class PositionOffset {
    int dRow;
    int dCol;

    /**
     * Creates a new PositionOffset.
     *
     * @param dRow The row delta.
     * @param dCol The column delta.
     */
    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }
    public PositionOffset() {
    }
    /**
     * Gets the row delta.
     *
     * @return The row delta.
     */
    public int getDRow() {
        return dRow;
    }

    /**
     * Sets the row delta.
     *
     * @param dRow The row delta.
     */
    public void setDRow(int dRow) {
        this.dRow = dRow;
    }

    /**
     * Gets the column delta.
     *
     * @return The column delta.
     */
    public int getDCol() {
        return dCol;
    }

    /**
     * Sets the column delta.
     *
     * @param dCol The column delta.
     */
    public void setDCol(int dCol) {
        this.dCol = dCol;
    }
}
