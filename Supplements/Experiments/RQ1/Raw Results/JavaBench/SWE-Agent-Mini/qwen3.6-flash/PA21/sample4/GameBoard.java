import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Represents the game board grid.
 */
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
        this.player = null;
    }

    public GameBoard(int numRows, int numCols, Cell[][] board, Player player) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = Objects.requireNonNull(player);
    }

    /**
     * Gets the number of rows.
     *
     * @return the number of rows.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Sets the number of rows.
     *
     * @param numRows the number of rows.
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * Gets the number of columns.
     *
     * @return the number of columns.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Sets the number of columns.
     *
     * @param numCols the number of columns.
     */
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    /**
     * Gets the board array.
     *
     * @return the board array.
     */
    public Cell[][] getBoard() {
        return board;
    }

    /**
     * Sets the board array.
     *
     * @param board the board array.
     */
    public void setBoard(Cell[][] board) {
        this.board = board;
    }

    /**
     * Gets the player.
     *
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player.
     *
     * @param player the player.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the cell at the given row.
     *
     * @param r the row index.
     * @return the row array.
     */
    public Cell[] getRow(int r) {
        return board[r];
    }

    /**
     * Gets the cell at the given column.
     *
     * @param c the column index.
     * @return the column array.
     */
    public Cell[] getCol(int c) {
        Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    /**
     * Gets the cell at the given position.
     *
     * @param r the row index.
     * @param c the column index.
     * @return the Cell at the given position.
     */
    public Cell getCell(int r, int c) {
        return board[r][c];
    }

    /**
     * Gets the number of gems remaining on the board.
     *
     * @return the number of gems.
     */
    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    EntityCell entityCell = (EntityCell) cell;
                    if (entityCell.getEntity() instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Gets the EntityCell at the given position.
     *
     * @param r the row index.
     * @param c the column index.
     * @return the EntityCell at the given position.
     */
    public EntityCell getEntityCell(int r, int c) {
        return (EntityCell) board[r][c];
    }

    /**
     * Gets the EntityCell at the given position.
     *
     * @param position the position.
     * @return the EntityCell at the given position.
     */
    public EntityCell getEntityCell(Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }

    /**
     * Removes the player from the board at their current position.
     */
    public void removePlayer() {
        if (player != null && player.getOwner() != null) {
            EntityCell owner = player.getOwner();
            owner.setentity(null);
        }
        player = null;
    }

    /**
     * Places the player at the given EntityCell position.
     *
     * @param position the position to place the player.
     */
    public void placePlayer(Position position) {
        EntityCell cell = getEntityCell(position);
        Player newPlayer = new Player();
        cell.setentity(newPlayer);
        this.player = newPlayer;
    }

    /**
     * Places the player directly on the given Player entity's owner cell.
     *
     * @param player the player entity.
     */
    public void placePlayer(Player player) {
        this.player = Objects.requireNonNull(player);
        if (player.getOwner() != null) {
            player.getOwner().setentity(player);
        }
    }
}
