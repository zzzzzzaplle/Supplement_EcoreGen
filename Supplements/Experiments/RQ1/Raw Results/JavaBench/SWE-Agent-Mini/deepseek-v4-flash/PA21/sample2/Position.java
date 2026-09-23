/**
 * Represents a position on the game board.
 */
public class Position {

    private int row;
    private int col;

    public Position() {
    }

    public Position(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    public Position offsetBy(final int dRow, final int dCol) {
        return new Position(this.row + dRow, this.col + dCol);
    }

    public Position offsetBy(final PositionOffset offset) {
        return new Position(this.row + offset.getDRow(), this.col + offset.getDCol());
    }

    public Position offsetByOrNull(final int dRow, final int dCol, final int numRows, final int numCols) {
        final int newRow = this.row + dRow;
        final int newCol = this.col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public Position offsetByOrNull(final PositionOffset offset, final int numRows, final int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getDCol(), numRows, numCols);
    }

    public int getRow() {
        return row;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(final int col) {
        this.col = col;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
