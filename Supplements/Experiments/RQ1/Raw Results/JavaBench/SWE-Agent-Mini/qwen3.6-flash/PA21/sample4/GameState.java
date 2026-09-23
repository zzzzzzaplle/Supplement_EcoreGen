import java.util.Objects;

/**
 * Manages the overall game state including score, lives, and win/loss conditions.
 */
public class GameState {

    public static final int UNLIMITED_LIVES = -1;

    private int numDeaths;
    private int numMoves;
    private int numLives;
    private int initialNumOfGems;
    private GameBoard gameBoard;
    private MoveStack moveStack;
    private GameBoardController gameBoardController;
    private GameBoardView gameBoardView;
    private int undoCount;

    public GameState() {
    }

    public GameState(GameBoard gameBoard) {
        this(gameBoard, 0);
    }

    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numMoves = 0;
        this.numDeaths = 0;
        this.numLives = numLives;
        this.undoCount = 0;
    }

    /**
     * Gets the number of deaths.
     *
     * @return the number of deaths.
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * Sets the number of deaths.
     *
     * @param numDeaths the number of deaths.
     */
    public void setNumDeaths(int numDeaths) {
        this.numDeaths = numDeaths;
    }

    /**
     * Gets the number of moves.
     *
     * @return the number of moves.
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * Sets the number of moves.
     *
     * @param numMoves the number of moves.
     */
    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    /**
     * Gets the number of lives.
     *
     * @return the number of lives, or Integer.MAX_VALUE if unlimited.
     */
    public int getNumLives() {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    /**
     * Sets the number of lives.
     *
     * @param numLives the number of lives.
     */
    public void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    /**
     * Gets the initial number of gems.
     *
     * @return the initial number of gems.
     */
    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    /**
     * Sets the initial number of gems.
     *
     * @param initialNumOfGems the initial number of gems.
     */
    public void setInitialNumOfGems(int initialNumOfGems) {
        this.initialNumOfGems = initialNumOfGems;
    }

    /**
     * Gets the game board.
     *
     * @return the game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the game board.
     *
     * @param gameBoard the game board.
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Gets the move stack.
     *
     * @return the move stack.
     */
    public MoveStack getMoveStack() {
        return moveStack;
    }

    /**
     * Sets the move stack.
     *
     * @param moveStack the move stack.
     */
    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = Objects.requireNonNull(moveStack);
    }

    /**
     * Gets the game board controller.
     *
     * @return the game board controller.
     */
    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    /**
     * Sets the game board controller.
     *
     * @param gameBoardController the game board controller.
     */
    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = Objects.requireNonNull(gameBoardController);
    }

    /**
     * Gets the game board view.
     *
     * @return the game board view.
     */
    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    /**
     * Sets the game board view.
     *
     * @param gameBoardView the game board view.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = Objects.requireNonNull(gameBoardView);
    }

    /**
     * Gets the number of gems remaining on the board.
     *
     * @return the number of gems.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Gets the current score.
     *
     * @return the score.
     */
    public int getScore() {
        int collectedGems = initialNumOfGems - getNumGems();
        int boardSize = gameBoard.numRows * gameBoard.numCols;
        return boardSize + (collectedGems * 10) - (numMoves * 1) - (undoCount * 2) - (numDeaths * 4);
    }

    /**
     * Checks if the player has won (all gems collected).
     *
     * @return true if the player has won.
     */
    public boolean hasWon() {
        return getNumGems() <= 0;
    }

    /**
     * Checks if the player has lost (no lives left and not unlimited).
     *
     * @return true if the player has lost.
     */
    public boolean hasLost() {
        if (hasUnlimitedLives()) {
            return false;
        }
        return numLives <= 0;
    }

    /**
     * Checks if the player has unlimited lives.
     *
     * @return true if unlimited lives.
     */
    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    /**
     * Increases the number of lives.
     *
     * @param delta the amount to increase by.
     * @return the new number of lives.
     */
    public int increaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the number of lives.
     *
     * @param delta the amount to decrease by.
     * @return the new number of lives.
     */
    public int decreaseNumLives(int delta) {
        if (hasUnlimitedLives()) {
            return Integer.MAX_VALUE;
        }
        numLives -= delta;
        return numLives;
    }

    /**
     * Decreases the number of lives by one.
     *
     * @return the new number of lives.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves by one.
     *
     * @return the new number of moves.
     */
    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    /**
     * Increments the number of deaths by one.
     *
     * @return the new number of deaths.
     */
    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    /**
     * Increments the undo count.
     *
     * @return the new undo count.
     */
    public int incrementUndoCount() {
        undoCount++;
        return undoCount;
    }
}
