/**
 * Represents a 2D grid coordinate.
 */
public class Position {
    private int row;
    private int col;

    /**
     * Creates a new Position with the specified row and column.
     *
     * @param row The row coordinate.
     * @param col The column coordinate.
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public Position()
    {
    }
    /**
     * Creates a Position offset by the given deltas.
     *
     * @param dRow The row delta.
     * @param dCol The column delta.
     * @return A new Position offset by dRow and dCol.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Creates a Position offset by the given PositionOffset.
     *
     * @param offset The position offset.
     * @return A new Position offset by the given offset.
     */
    public Position offsetBy(PositionOffset offset) {
        return offsetBy(offset.dRow, offset.dCol);
    }

    /**
     * Creates a Position offset by the given deltas, or null if the result would be out of bounds.
     *
     * @param dRow    The row delta.
     * @param dCol    The column delta.
     * @param numRows The number of rows in the board.
     * @param numCols The number of columns in the board.
     * @return A new Position offset by dRow and dCol, or null if out of bounds.
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
     * Creates a Position offset by the given PositionOffset, or null if the result would be out of bounds.
     *
     * @param offset   The position offset.
     * @param numRows  The number of rows in the board.
     * @param numCols  The number of columns in the board.
     * @return A new Position offset by the given offset, or null if out of bounds.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.dRow, offset.dCol, numRows, numCols);
    }

    /**
     * Gets the row coordinate.
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
     * Gets the column coordinate.
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
}
