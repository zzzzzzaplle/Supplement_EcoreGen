/**
 * Represents the full state of the game.
 */
public class GameState {

    public static final int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;

    public GameState() {
    }

    public GameState(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.numLives = UNLIMITED_LIVES;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public GameState(final GameBoard gameBoard, final int numLives) {
        this.gameBoard = gameBoard;
        this.numLives = numLives;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.moveStack = new MoveStack();
    }

    public boolean hasWon() {
        return getNumGems() == 0;
    }

    public boolean hasLost() {
        if (hasUnlimitedLives()) {
            return false;
        }
        return getNumLives() == 0;
    }

    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    public int increaseNumLives(final int delta) {
        if (!hasUnlimitedLives()) {
            this.numLives += delta;
        }
        return getNumLives();
    }

    public int decreaseNumLives(final int delta) {
        if (!hasUnlimitedLives()) {
            this.numLives -= delta;
            if (this.numLives < 0) {
                this.numLives = 0;
            }
        }
        return getNumLives();
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

    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    public int getScore() {
        final int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        final int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        return initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (moveStack.getPopCount() * 2) - (numDeaths * 4);
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

    public void setGameBoard(final GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public MoveStack getMoveStack() {
        return moveStack;
    }

    public void setMoveStack(final MoveStack moveStack) {
        this.moveStack = moveStack;
    }

    public int getNumDeaths() {
        return numDeaths;
    }

    public void setNumDeaths(final int numDeaths) {
        this.numDeaths = numDeaths;
    }

    public int getNumMoves() {
        return numMoves;
    }

    public void setNumMoves(final int numMoves) {
        this.numMoves = numMoves;
    }

    public int getNumLives() {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    public void setNumLives(final int numLives) {
        this.numLives = numLives;
    }

    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    public void setInitialNumOfGems(final int initialNumOfGems) {
        this.initialNumOfGems = initialNumOfGems;
    }
}
