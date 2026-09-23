import java.util.Objects;

public class GameBoard {

    private int numRows;
    private int numCols;
    private Cell[][] board;

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public Cell getRow(int r) {
        return board[r];
    }

    public Cell getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        Cell cell = board[r][c];
        return (EntityCell) cell;
    }

    public EntityCell getEntityCell(Position position) {
        int r = position.getRow();
        int c = position.getCol();
        return getEntityCell(r, c);
    }
}
