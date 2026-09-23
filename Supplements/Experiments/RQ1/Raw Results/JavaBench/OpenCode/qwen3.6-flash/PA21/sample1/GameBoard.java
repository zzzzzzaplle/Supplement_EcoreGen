public class GameBoard {
    private final int numRows;
    private final int numCols;
    private final Cell[][] board;
    private final Player player;

    public GameBoard(final int numRows, final int numCols, final Cell[][] board) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = board;
        this.player = findPlayer();
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
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

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] instanceof EntityCell) {
                    final Entity entity = ((EntityCell) board[r][c]).getEntity();
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

    public Player getPlayer() {
        return player;
    }

    private Player findPlayer() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (board[r][c] instanceof EntityCell) {
                    final Entity entity = ((EntityCell) board[r][c]).getEntity();
                    if (entity instanceof Player) {
                        return (Player) entity;
                    }
                }
            }
        }
        return null;
    }
}
