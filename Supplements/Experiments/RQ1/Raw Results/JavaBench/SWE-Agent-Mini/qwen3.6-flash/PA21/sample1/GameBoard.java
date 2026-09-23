import java.util.Objects;

/**
 * Represents the game board grid.
 */
public class GameBoard {
    public int numRows;
    public int numCols;
    public Cell[][] board;
    public Player player;

    /**
     * Creates a new GameBoard with the specified dimensions and board cells.
     *
     * @param numRows  The number of rows.
     * @param numCols  The number of columns.
     * @param board    The 2D array of cells.
     * @throws NullPointerException if any cell in the board is null.
     */
    public GameBoard(int numRows, int numCols, Cell[][] board) {
        Objects.requireNonNull(board);
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        // Find the player
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell ec = (EntityCell) cell;
                    if (ec.getEntity() instanceof Player) {
                        this.player = (Player) ec.getEntity();
                    }
                }
            }
        }
    }

    /**
     * Creates a new GameBoard with the specified dimensions, board cells, and player.
     *
     * @param numRows  The number of rows.
     * @param numCols  The number of columns.
     * @param board    The 2D array of cells.
     * @param player   The player entity.
     * @throws NullPointerException if board is null.
     */
    public GameBoard(int numRows, int numCols, Cell[][] board, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = player;
    }

    /**
     * Gets the row at the specified index.
     *
     * @param r The row index.
     * @return The row array.
     */
    public Cell[] getRow(int r) {
        return board[r];
    }

    /**
     * Gets the column at the specified index.
     *
     * @param c The column index.
     * @return The column array.
     */
    public Cell[] getCol(int c) {
        Cell[] colArray = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            colArray[r] = board[r][c];
        }
        return colArray;
    }

    /**
     * Gets the cell at the specified position.
     *
     * @param r The row index.
     * @param c The column index.
     * @return The cell at the specified position.
     */
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    /**
     * Gets a cell by position.
     *
     * @param position The position.
     * @return The cell at the specified position.
     */
    public Cell getCell(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    /**
     * Counts the number of gems on the board.
     *
     * @return The number of gems.
     */
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

    /**
     * Gets the EntityCell at the specified position.
     *
     * @param r The row index.
     * @param c The column index.
     * @return The EntityCell at the specified position.
     */
    public EntityCell getEntityCell(int r, int c) {
        return (EntityCell) board[r][c];
    }

    /**
     * Gets the EntityCell at the specified position.
     *
     * @param position The position.
     * @return The EntityCell at the specified position.
     */
    public EntityCell getEntityCell(Position position) {
        return (EntityCell) board[position.getRow()][position.getCol()];
    }

    /**
     * Gets the number of rows.
     *
     * @return The number of rows.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Sets the number of rows.
     *
     * @param numRows The number of rows.
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * Gets the number of columns.
     *
     * @return The number of columns.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Sets the number of columns.
     *
     * @param numCols The number of columns.
     */
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    /**
     * Gets the board array.
     *
     * @return The board array.
     */
    public Cell[][] getBoard() {
        return board;
    }

    /**
     * Sets the board array.
     *
     * @param board The board array.
     */
    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    /**
     * Gets the player.
     *
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player.
     *
     * @param player The player.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
