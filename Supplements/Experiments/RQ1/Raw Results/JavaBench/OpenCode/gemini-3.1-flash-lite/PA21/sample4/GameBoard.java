public class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    public Player player;

    public GameBoard() {}

    public Cell[] getRow(int r) {
        return null;
    }

    public Cell[] getCol(int c) {
        return null;
    }

    public Cell getCell(int r, int c) {
        return null;
    }

    public int getNumGems() {
        return 0;
    }

    public EntityCell getEntityCell(int r, int c) {
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        return null;
    }

    public int getNumRows() { return numRows; }
    public int getNumCols() { return numCols; }
}
