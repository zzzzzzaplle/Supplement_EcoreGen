import java.util.Objects;

public class GameBoard {
    public final int numRows;
    public final int numCols;
    private Cell[][] board;

    public GameBoard() {
        this.numRows = 0;
        this.numCols = 0;
        this.board = new Cell[0][0];
    }

    public GameBoard(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; ++r) {
            for (int c = 0; c < numCols; ++c) {
                Position pos = new Position(r, c);
                board[r][c] = new Wall(pos);
            }
        }
    }

    public GameBoard(int numRows, int numCols, Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
    }

    public Cell[] getRow(int r) {
        return board[r];
    }

    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; ++r) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; ++r) {
            for (int c = 0; c < numCols; ++c) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell entityCell) {
                    Entity e = entityCell.getEntity();
                    if (e instanceof Gem) {
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(int r, int c) {
        Cell cell = board[r][c];
        if (cell instanceof EntityCell entityCell) {
            return entityCell;
        }
        return null;
    }

    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
