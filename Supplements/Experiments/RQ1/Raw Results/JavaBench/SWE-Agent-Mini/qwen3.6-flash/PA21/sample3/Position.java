import java.util.Objects;

/**
 * Represents a 2D position in the game grid.
 */
public class Position {
    private int row;
    private int col;

    /**
     * Creates a position at (0, 0).
     */
    public Position() {
        this(0, 0);
    }

    /**
     * Creates a position at the given row and column.
     *
     * @param row The row coordinate.
     * @param col The column coordinate.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row coordinate.
     *
     * @return The row coordinate.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row coordinate.
     *
     * @param row The row coordinate.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Returns the column coordinate.
     *
     * @return The column coordinate.
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the column coordinate.
     *
     * @param col The column coordinate.
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Returns a new position offset by the given amounts.
     *
     * @param dRow The row offset.
     * @param dCol The column offset.
     * @return A new position offset by the given amounts.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Returns a new position offset by the given PositionOffset.
     *
     * @param offset The offset to apply.
     * @return A new position offset by the given offset.
     */
    public Position offsetBy(PositionOffset offset) {
        Objects.requireNonNull(offset);
        return offsetBy(offset.getDRow(), offset.getDCol());
    }

    /**
     * Returns a new position offset by the given amounts if within bounds.
     *
     * @param dRow    The row offset.
     * @param dCol    The column offset.
     * @param numRows The number of rows in the grid.
     * @param numCols The number of columns in the grid.
     * @return A new position if in bounds, null otherwise.
     */
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    /**
     * Returns a new position offset by the given PositionOffset if within bounds.
     *
     * @param offset  The offset to apply.
     * @param numRows The number of rows in the board.
     * @param numCols The number of columns in the board.
     * @return A new position if within bounds, null otherwise.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        Objects.requireNonNull(offset);
        int newRow = this.row + offset.getDRow();
        int newCol = this.col + offset.getDCol();
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }
}
