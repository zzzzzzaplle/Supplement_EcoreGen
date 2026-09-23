import java.util.Objects;

/**
 * Controls the game logic including moves, undo, and game state management.
 */
public class GameController {
    private GameState gameState;

    /**
     * Creates a default GameController.
     */
    public GameController() {
    }

    /**
     * Creates a GameController with the specified game state.
     *
     * @param gameState The game state.
     */
    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Returns the game state.
     *
     * @return The game state.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the game state.
     *
     * @param gameState The game state.
     */
    public void setGameState(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a move in the specified direction.
     *
     * @param direction The direction to move.
     * @return The MoveResult of the move.
     */
    public MoveResult processMove(Direction direction) {
        Objects.requireNonNull(direction);

        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves don't change counters and are not pushed to the stack
            return result;
        }

        if (result instanceof Alive) {
            Alive aliveMove = (Alive) result;
            gameState.incrementNumMoves();

            // Apply collected ExtraLife effects
            for (Position pos : aliveMove.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            // Push to MoveStack
            gameState.getMoveStack().push(result);
        }

        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            // Dead moves are not pushed to the MoveStack
        }

        return result;
    }

    /**
     * Processes an undo action.
     *
     * @return True if an undo was performed, false otherwise.
     */
    public boolean processUndo() {
        MoveResult topMove = gameState.getMoveStack().peek();

        if (topMove == null || !(topMove instanceof Alive)) {
            return false;
        }

        // Only Alive moves are undoable
        Alive aliveMove = (Alive) topMove;
        gameState.getGameBoardController().undoMove(topMove);

        // Reverse finite ExtraLife gains
        if (!gameState.hasUnlimitedLives()) {
            gameState.decreaseNumLives(aliveMove.getCollectedExtraLives().size());
        }

        // Pop the move
        gameState.getMoveStack().pop();

        return true;
    }
}
