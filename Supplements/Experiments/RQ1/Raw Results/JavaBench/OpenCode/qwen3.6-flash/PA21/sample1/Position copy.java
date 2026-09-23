import java.util.Objects;

public class Position {
    private int row;
    private int col;

    public Position() {
        this.row = 0;
        this.col = 0;
    }

    public Position(final int row, final int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Position offsetBy(final int dRow, final int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    public Position offsetBy(final PositionOffset offset) {
        return offsetBy(offset.getRowOffset(), offset.getColOffset());
    }

    public Position offsetByOrNull(final int dRow, final int dCol, final int numRows, final int numCols) {
        final int newRow = row + dRow;
        final int newCol = col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public Position offsetByOrNull(final PositionOffset offset, final int numRows, final int numCols) {
        return offsetByOrNull(offset.getRowOffset(), offset.getColOffset(), numRows, numCols);
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
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}
