/**
 * The game board containing all cells, the player, and board dimensions.
 */
public class GameBoard {

    private int numRows;
    private int numCols;
    private Cell[][] board;
    private Player player;

    public GameBoard() {
    }

    public GameBoard(final int numRows, final int numCols, final Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = findPlayer();
    }

    private Player findPlayer() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                final Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    final Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Player) {
                        return (Player) entity;
                    }
                }
            }
        }
        return null;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(final int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(final int numCols) {
        this.numCols = numCols;
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void setBoard(final Cell[][] board) {
        this.board = board;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public Cell[] getRow(final int r) {
        return board[r];
    }

    public Cell[] getCol(final int c) {
        final Cell[] col = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            col[r] = board[r][c];
        }
        return col;
    }

    public Cell getCell(final int r, final int c) {
        return board[r][c];
    }

    public Cell getCell(final Position position) {
        return board[position.getRow()][position.getCol()];
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                final Cell cell = board[r][c];
                if (cell instanceof EntityCell) {
                    final Entity entity = ((EntityCell) cell).getEntity();
                    if (entity instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public EntityCell getEntityCell(final int r, final int c) {
        final Cell cell = board[r][c];
        if (cell instanceof EntityCell) {
            return (EntityCell) cell;
        }
        return null;
    }

    public EntityCell getEntityCell(final Position position) {
        return getEntityCell(position.getRow(), position.getCol());
    }
}
