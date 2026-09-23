public class Position {
    private int row;
    private int col;

    public Position() {}

    public Position(int row, int col) { this.row = row; this.col = col; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public Position offsetBy(int dRow, int dCol) { return new Position(row + dRow, col + dCol); }
    public Position offsetBy(PositionOffset offset) { return new Position(row + offset.getDRow(), col + offset.getDCol()); }
    public Position offsetByOrNull(int dRow, int dCol, int numRows, int numCols) { return null; }
    public Position offsetByOrNull(PositionOffset offset, int numRows, int numCols) { return null; }
}
