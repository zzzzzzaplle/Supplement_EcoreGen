public class GameState {
    public static final int UNLIMITED_LIVES = -1;
    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private final GameBoard gameBoard;
    private final MoveStack moveStack;

    public GameState(GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.moveStack = new MoveStack();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.initialNumOfGems = countInitialGems();
    }

    private int countInitialGems() {
        int count = 0;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        if (numLives == UNLIMITED_LIVES) {
            return false;
        }
        return numLives <= 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    public int increaseNumLives(int delta) {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    public int decreaseNumLives(int delta) {
        if (numLives == UNLIMITED_LIVES) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        if (numLives < 0) {
            numLives = UNLIMITED_LIVES;
        }
        return numLives;
    }

    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    public int incrementNumMoves() {
        return ++numMoves;
    }

    public int incrementNumDeaths() {
        return ++numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < gameBoard.getNumRows(); r++) {
            for (int c = 0; c < gameBoard.getNumCols(); c++) {
                Cell cell = gameBoard.getCell(r, c);
                if (cell instanceof EntityCell) {
                    Entity e = ((EntityCell) cell).getEntity();
                    if (e instanceof Gem) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public int getScore() {
        int boardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - getNumGems();
        int undoes = moveStack.getUndoCount();
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undoes * 2) - (numDeaths * 4);
    }

    public GameBoardController getGameBoardController() {
        return new GameBoardController(gameBoard);
    }

    public GameBoardView getGameBoardView() {
        return new GameBoardView(gameBoard);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public int getNumLives() {
        return numLives == UNLIMITED_LIVES ? Integer.MAX_VALUE : numLives;
    }

    public void collectGem(Position pos) {
    }

    public void collectExtraLife(Position pos) {
    }

    public void undoMove(MoveResult result) {
    }
}
