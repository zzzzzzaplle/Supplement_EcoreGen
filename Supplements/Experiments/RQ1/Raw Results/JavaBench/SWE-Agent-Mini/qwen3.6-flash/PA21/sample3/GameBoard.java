import java.util.Objects;

public class GameBoard {
    private int numRows;
    private int numCols;
    private Cell[][] board;
    private Player player;

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

    public Cell[] getRow(int r) { return board[r]; }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(int r, int c) { return board[r][c]; }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Gem) count++;
                }
            }
        }
        return count;
    }

    public Position findPlayerPosition() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Player) return new Position(r, c);
                }
            }
        }
        return null;
    }
}
