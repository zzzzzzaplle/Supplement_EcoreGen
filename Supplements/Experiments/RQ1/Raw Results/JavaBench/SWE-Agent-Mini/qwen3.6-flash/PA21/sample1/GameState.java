import java.util.Objects;

/**
 * Manages the overall game state.
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
    private int numUndos = 0;

    /**
     * Creates a new GameState with unlimited lives.
     *
     * @param gameBoard The game board.
     */
    public GameState(GameBoard gameBoard) {
        this(gameBoard, UNLIMITED_LIVES);
    }

    /**
     * Creates a new GameState with the specified number of lives.
     *
     * @param gameBoard The game board.
     * @param numLives  The initial number of lives.
     */
    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.initialNumOfGems = gameBoard.getNumGems();
        this.numDeaths = 0;
        this.numMoves = 0;
        this.numLives = numLives;
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
        this.moveStack = new MoveStack();
    }

    /**
     * Checks if the player has won (no gems left).
     *
     * @return true if the player has won.
     */
    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    /**
     * Checks if the player has lost (lives reached zero).
     *
     * @return true if the player has lost.
     */
    public boolean hasLost() {
        if (numLives < 0) {
            return false;
        }
        return numLives == 0;
    }

    /**
     * Checks if the player has unlimited lives.
     *
     * @return true if unlimited lives are enabled.
     */
    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    /**
     * Increases the number of lives by the given delta.
     *
     * @param delta The amount to increase.
     * @return The new number of lives.
     */
    public int increaseNumLives(int delta) {
        if (numLives < 0) {
            // Already unlimited
            return numLives;
        }
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the number of lives by the given delta.
     *
     * @param delta The amount to decrease.
     * @return The new number of lives.
     */
    public int decreaseNumLives(int delta) {
        if (numLives < 0) {
            return numLives;
        }
        int newLives = numLives - delta;
        if (newLives < 0) {
            numLives = 0;
        } else {
            numLives = newLives;
        }
        return numLives;
    }

    /**
     * Decreases the number of lives by one.
     *
     * @return The new number of lives.
     */
    public int decrementNumLives() {
        return decreaseNumLives(1);
    }

    /**
     * Increments the number of moves.
     *
     * @return The new number of moves.
     */
    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    /**
     * Increments the number of deaths.
     *
     * @return The new number of deaths.
     */
    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    /**
     * Gets the number of gems on the board.
     *
     * @return The number of gems.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Gets the score.
     *
     * @return The calculated score.
     */
    public int getScore() {
        int initialBoardSize = gameBoard.getNumRows() * gameBoard.getNumCols();
        int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        int score = initialBoardSize + (collectedGems * 10) - (numMoves * 1) - (numUndos * 2) - (numDeaths * 4);
        return score;
    }

    /**
     * Gets the number of lives, returning Integer.MAX_VALUE if unlimited.
     *
     * @return The number of lives.
     */
    public int getNumLives() {
        if (numLives < 0) {
            return Integer.MAX_VALUE;
        }
        return numLives;
    }

    /**
     * Gets the game board controller.
     *
     * @return The game board controller.
     */
    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    /**
     * Gets the game board view.
     *
     * @return The game board view.
     */
    public GameBoardView getGameBoardView() {
        return gameBoardView;
    }

    /**
     * Gets the game board.
     *
     * @return The game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Gets the move stack.
     *
     * @return The move stack.
     */
    public MoveStack getMoveStack() {
        return moveStack;
    }

    /**
     * Gets the number of deaths.
     *
     * @return The number of deaths.
     */
    public int getNumDeaths() {
        return numDeaths;
    }

    /**
     * Sets the number of deaths.
     *
     * @param numDeaths The number of deaths.
     */
    public void setNumDeaths(int numDeaths) {
        this.numDeaths = numDeaths;
    }

    /**
     * Gets the number of moves.
     *
     * @return The number of moves.
     */
    public int getNumMoves() {
        return numMoves;
    }

    /**
     * Sets the number of moves.
     *
     * @param numMoves The number of moves.
     */
    public void setNumMoves(int numMoves) {
        this.numMoves = numMoves;
    }

    /**
     * Gets the internal number of lives (before transformation to MAX_VALUE).
     *
     * @return The internal number of lives.
     */
    public int getNumLivesInternal() {
        return numLives;
    }

    /**
     * Sets the internal number of lives.
     *
     * @param numLives The internal number of lives.
     */
    public void setNumLives(int numLives) {
        this.numLives = numLives;
    }

    /**
     * Gets the initial number of gems.
     *
     * @return The initial number of gems.
     */
    public int getInitialNumOfGems() {
        return initialNumOfGems;
    }

    /**
     * Sets the initial number of gems.
     *
     * @param initialNumOfGems The initial number of gems.
     */
    public void setInitialNumOfGems(int initialNumOfGems) {
        this.initialNumOfGems = initialNumOfGems;
    }

    /**
     * Gets the number of undos.
     *
     * @return The number of undos.
     */
    public int getNumUndos() {
        return numUndos;
    }

    /**
     * Sets the number of undos.
     *
     * @param numUndos The number of undos.
     */
    public void setNumUndos(int numUndos) {
        this.numUndos = numUndos;
    }

    /**
     * Sets the game board controller.
     *
     * @param gameBoardController The game board controller.
     */
    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
    }

    /**
     * Sets the game board view.
     *
     * @param gameBoardView The game board view.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }

    /**
     * Sets the move stack.
     *
     * @param moveStack The move stack.
     */
    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = moveStack;
    }
}
