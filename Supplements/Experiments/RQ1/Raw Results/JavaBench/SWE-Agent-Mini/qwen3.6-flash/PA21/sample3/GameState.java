import java.util.Objects;

/**
 * Holds the overall game state including score, lives, moves, and references to game components.
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

    /**
     * Creates a default GameState.
     */
    public GameState() {
    }

    /**
     * Creates a new game state with the specified game board and unlimited lives.
     *
     * @param gameBoard The game board.
     */
    public GameState(GameBoard gameBoard) {
        this(gameBoard, -1);
    }

    /**
     * Creates a new game state with the specified game board and number of lives.
     *
     * @param gameBoard The game board.
     * @param numLives  The number of lives.
     */
    public GameState(GameBoard gameBoard, int numLives) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.numLives = numLives;
        this.numDeaths = 0;
        this.numMoves = 0;
        this.initialNumOfGems = gameBoard.getNumGems();
        this.moveStack = new MoveStack();
        this.gameBoardController = new GameBoardController(gameBoard);
        this.gameBoardView = new GameBoardView(gameBoard);
    }

    /**
     * Returns the number of deaths.
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
     * Returns the number of moves.
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
     * Returns the number of lives. If negative (unlimited), returns Integer.MAX_VALUE.
     *
     * @return The number of lives.
     */
    public int getNumLives() {
        return numLives < 0 ? Integer.MAX_VALUE : numLives;
    }

    /**
     * Returns the actual stored numLives value.
     *
     * @return The stored number of lives value.
     */
    public int getStoredNumLives() {
        return numLives;
    }

    /**
     * Sets the stored number of lives.
     *
     * @param numLives The stored number of lives.
     */
    public void setStoredNumLives(int numLives) {
        this.numLives = numLives;
    }

    /**
     * Returns the initial number of gems on the board.
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
     * Returns the game board.
     *
     * @return The game board.
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    /**
     * Sets the game board.
     *
     * @param gameBoard The game board.
     */
    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = Objects.requireNonNull(gameBoard);
    }

    /**
     * Returns the move stack.
     *
     * @return The move stack.
     */
    public MoveStack getMoveStack() {
        return moveStack;
    }

    /**
     * Sets the move stack.
     *
     * @param moveStack The move stack.
     */
    public void setMoveStack(MoveStack moveStack) {
        this.moveStack = Objects.requireNonNull(moveStack);
    }

    /**
     * Checks if the player has won (all gems collected).
     *
     * @return True if all gems have been collected.
     */
    public boolean hasWon() {
        return gameBoard.getNumGems() == 0;
    }

    /**
     * Checks if the player has lost (lives reached zero and not in unlimited mode).
     *
     * @return True if the player has lost.
     */
    public boolean hasLost() {
        return numLives >= 0 && numLives == 0;
    }

    /**
     * Checks if the player has unlimited lives.
     *
     * @return True if unlimited lives is enabled.
     */
    public boolean hasUnlimitedLives() {
        return numLives < 0;
    }

    /**
     * Increases the number of lives by the given delta.
     *
     * @param delta The amount to increase by.
     * @return The new number of lives.
     */
    public int increaseNumLives(int delta) {
        numLives += delta;
        return numLives;
    }

    /**
     * Decreases the number of lives by the given delta (only if finite lives).
     *
     * @param delta The amount to decrease by.
     * @return The new number of lives.
     */
    public int decreaseNumLives(int delta) {
        if (numLives >= 0) {
            numLives -= delta;
        }
        return numLives;
    }

    /**
     * Decreases the number of lives by one (only if finite lives).
     *
     * @return The new number of lives.
     */
    public int decrementNumLives() {
        if (numLives >= 0) {
            numLives--;
        }
        return numLives;
    }

    /**
     * Increments the number of moves by one.
     *
     * @return The new number of moves.
     */
    public int incrementNumMoves() {
        numMoves++;
        return numMoves;
    }

    /**
     * Increments the number of deaths by one.
     *
     * @return The new number of deaths.
     */
    public int incrementNumDeaths() {
        numDeaths++;
        return numDeaths;
    }

    /**
     * Returns the number of gems remaining on the board.
     *
     * @return The number of gems.
     */
    public int getNumGems() {
        return gameBoard.getNumGems();
    }

    /**
     * Calculates and returns the current score.
     *
     * @return The calculated score.
     */
    public int getScore() {
        int collectedGems = initialNumOfGems - gameBoard.getNumGems();
        int undos = moveStack.getPopCount();
        return initialNumOfGems + (collectedGems * 10) - (numMoves * 1) - (undos * 2) - (numDeaths * 4);
    }

    /**
     * Returns the game board controller.
     *
     * @return The game board controller.
     */
    public GameBoardController getGameBoardController() {
        return gameBoardController;
    }

    /**
     * Sets the game board controller.
     *
     * @param gameBoardController The game board controller.
     */
    public void setGameBoardController(GameBoardController gameBoardController) {
        this.gameBoardController = Objects.requireNonNull(gameBoardController);
    }

    /**
     * Returns the game board view.
     *
     * @return The game board view.
     */
    public GameBoardView getGameBoardView() {
        return gameBoardView;
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
     * Returns the direction character for the given direction.
     *
     * @param direction The direction.
     * @return The direction character ('U', 'D', 'L', or 'R').
     */
    public char getDirectionChar(Direction direction) {
        Objects.requireNonNull(direction);
        switch (direction) {
            case UP:
                return 'U';
            case DOWN:
                return 'D';
            case LEFT:
                return 'L';
            case RIGHT:
                return 'R';
            default:
                throw new IllegalStateException("Unknown direction: " + direction);
        }
    }
}
