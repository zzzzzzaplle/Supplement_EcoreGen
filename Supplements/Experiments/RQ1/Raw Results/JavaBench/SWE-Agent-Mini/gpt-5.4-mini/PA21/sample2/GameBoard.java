public class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    public Player player;

    public GameBoard() {
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
    }

    public int getNumRows() { return numRows; }
    public void setNumRows(int numRows) { this.numRows = numRows; }
    public int getNumCols() { return numCols; }
    public void setNumCols(int numCols) { this.numCols = numCols; }
    public Cell[][] getBoard() { return board; }
    public void setBoard(Cell[][] board) { this.board = board; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Cell[] getRow(int r) {
        return board[r];
    }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) col[r] = board[r][c];
        return col;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getNumGems() {
        int n = 0;
        for (int r = 0; r < numRows; r++) for (int c = 0; c < numCols; c++) if (board[r][c] instanceof EntityCell && ((EntityCell) board[r][c]).getEntity() instanceof Gem) n++;
        return n;
    }

    public EntityCell getEntityCell(int r, int c) {
        return (EntityCell) board[r][c];
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }
}
