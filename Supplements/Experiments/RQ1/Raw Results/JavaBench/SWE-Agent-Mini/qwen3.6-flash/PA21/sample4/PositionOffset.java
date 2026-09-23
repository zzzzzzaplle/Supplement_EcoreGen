/**
 * Represents an offset in terms of row and column deltas.
 */
public class PositionOffset {

    private int dRow;
    private int dCol;

    public PositionOffset() {
    }

    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    /**
     * Gets the row delta.
     *
     * @return the row delta.
     */
    public int getDRow() {
        return dRow;
    }

    /**
     * Sets the row delta.
     *
     * @param dRow the row delta.
     */
    public void setDRow(int dRow) {
        this.dRow = dRow;
    }

    /**
     * Gets the column delta.
     *
     * @return the column delta.
     */
    public int getDCol() {
        return dCol;
    }

    /**
     * Sets the column delta.
     *
     * @param dCol the column delta.
     */
    public void setDCol(int dCol) {
        this.dCol = dCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionOffset offset = (PositionOffset) o;
        return dRow == offset.dRow && dCol == offset.dCol;
    }

    @Override
    public int hashCode() {
        return 31 * dRow + dCol;
    }
}
