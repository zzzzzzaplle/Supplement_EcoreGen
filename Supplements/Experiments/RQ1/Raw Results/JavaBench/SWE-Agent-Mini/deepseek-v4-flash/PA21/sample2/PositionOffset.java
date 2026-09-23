/**
 * Represents a delta (offset) in row and column.
 */
public class PositionOffset {

    private int dRow;
    private int dCol;

    public PositionOffset() {
    }

    public PositionOffset(final int dRow, final int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {
        return dRow;
    }

    public void setDRow(final int dRow) {
        this.dRow = dRow;
    }

    public int getDCol() {
        return dCol;
    }

    public void setDCol(final int dCol) {
        this.dCol = dCol;
    }
}
