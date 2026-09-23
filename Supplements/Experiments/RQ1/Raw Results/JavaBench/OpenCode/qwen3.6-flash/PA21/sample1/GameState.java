public class GameState {
    public static final int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private final int initialNumOfGems;
    private final GameBoard gameBoard;
    private final MoveStack moveStack;

    public GameState(final GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.numMoves = 0;
        this.numDeaths = 0;
        this.moveStack = new MoveStack();
        this.initialNumOfGems = gameBoard.getNumGems();
    }

    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    public boolean hasLost() {
        if (numLives == UNLIMITED_LIVES) {
            return false;
        }
        return numLives <= 0;
    }

    public int increaseNumLives(final int delta) {
        return numLives += delta;
    }

    public int decreaseNumLives(final int delta) {
        return numLives -= delta;
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
        return gameBoard.getNumGems();
    }

    public int getScore() {
        final int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        final int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        final int moves = numMoves;
        final int undoes = moveStack.getPopCount();
        final int deaths = numDeaths;

        return initialBoardSize + (collectedGems * 10) - (moves * 1) - (undoes * 2) - (deaths * 4);
    }

    public boolean hasUnlimitedLives() {
        return numLives == UNLIMITED_LIVES;
    }

    public int getNumLives() {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        return numLives;
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
}
