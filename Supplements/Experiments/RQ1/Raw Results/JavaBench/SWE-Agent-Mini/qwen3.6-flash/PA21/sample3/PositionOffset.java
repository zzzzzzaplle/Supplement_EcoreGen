import java.util.Objects;

/**
 * Represents a position offset in the game grid.
 */
public class PositionOffset {
    private int dRow;
    private int dCol;

    public PositionOffset() {
        this(0, 0);
    }

    public PositionOffset(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {
        return dRow;
    }

    public void setDRow(int dRow) {
        this.dRow = dRow;
    }

    public int getDCol() {
        return dCol;
    }

    public void setDCol(int dCol) {
        this.dCol = dCol;
    }
}
