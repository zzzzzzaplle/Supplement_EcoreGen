public class Position {

    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Position offsetBy(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    public Position offsetBy(PositionOffset offset) {
        return offsetBy(offset.getDRow(), offset.getCol());
    }

    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) {
        int newRow = row + dRow;
        int newCol = col + dCol;
        if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
            return null;
        }
        return new Position(newRow, newCol);
    }

    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) {
        return offsetByOrNull(offset.getDRow(), offset.getCol(), numRows, numCols);
    }
}
