/**
 * Represents a 2D coordinate on the game board.
 */
public class Position {

    private int row;
    private int col;

    public Position() {
    }

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the row coordinate.
     *
     * @return the row coordinate.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row coordinate.
     *
     * @param row the row coordinate.
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Gets the column coordinate.
     *
     * @return the column coordinate.
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the column coordinate.
     *
     * @param col the column coordinate.
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Creates a new Position offset by the given row and column deltas.
     *
     * @param dRow the row delta.
     * @param dCol the column delta.
     * @return a new Position offset by the given deltas.
     */
    public Position offsetBy(int dRow, int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    /**
     * Creates a new Position offset by the given PositionOffset.
     *
     * @param offset the position offset.
     * @return a new Position offset by the given offset.
     */
    public Position offsetBy(PositionOffset offset) {
        return new Position(this.row + offset.getDRow(), this.col + offset.getDCol());
    }

    /**
     * Creates a new Position offset by the given row and column deltas,
     * or null if the result would be out of bounds.
     *
     * @param dRow     the row delta.
     * @param dCol     the column delta.
     * @param numRows  number of rows on the board.
     * @param numCols  number of columns on the board.
     * @return a new Position offset by the given deltas, or null if out of bounds.
     */
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = this.row + dRow;
        int newCol = this.col + dCol;
        if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numCols) {
            return new Position(newRow, newCol);
        }
        return null;
    }

    /**
     * Creates a new Position offset by the given PositionOffset,
     * or null if the result would be out of bounds.
     *
     * @param offset   the position offset.
     * @param numRows  number of rows on the board.
     * @param numCols  number of columns on the board.
     * @return a new Position offset by the given offset, or null if out of bounds.
     */
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
