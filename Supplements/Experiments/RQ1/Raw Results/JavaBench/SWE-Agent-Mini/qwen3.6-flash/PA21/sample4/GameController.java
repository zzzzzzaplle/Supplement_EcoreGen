import java.util.Objects;
import java.util.ArrayList;

/**
 * Controls the overall game flow including move processing and undo.
 */
public class GameController {

    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Gets the game state.
     *
     * @return the game state.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the game state.
     *
     * @param gameState the game state.
     */
    public void setGameState(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a move in the given direction.
     *
     * @param direction the direction to move.
     * @return a MoveResult describing the outcome.
     */
    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves don't change counters or get pushed
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;

            // Increment move count
            gameState.incrementNumMoves();

            // Apply collected extra lives
            for (Position pos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            // Push to move stack
            gameState.getMoveStack().push(alive);

            return result;
        }

        if (result instanceof Dead) {
            Dead dead = (Dead) result;

            // Increment move count and death count
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();

            // Decrease lives if not unlimited
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }

            // Dead moves are not pushed to the stack
            return result;
        }

        return result;
    }

    /**
     * Processes an undo of the last move.
     *
     * @return true if undo was successful, false otherwise.
     */
    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();

        if (moveStack.isEmpty()) {
            return false;
        }

        // Pop the last alive move
        MoveResult result = moveStack.pop();
        if (!(result instanceof Alive)) {
            return false;
        }

        Alive alive = (Alive) result;

        // Record the undo count (increment via gameState)
        gameState.incrementUndoCount();

        // Get the player
        Player player = gameState.getGameBoard().getPlayer();
        if (player == null) {
            return false;
        }

        // Remove player from current position
        gameState.getGameBoard().removePlayer();

        // Remove the collected gems from the board (put them back)
        for (Position pos : alive.getCollectedGems()) {
            EntityCell cell = gameState.getGameBoard().getEntityCell(pos);
            cell.setentity(new Gem());
        }

        // Reverse the collected extra lives
        for (Position pos : alive.getCollectedExtraLives()) {
            gameState.increaseNumLives(-1);
        }

        // Place player back at the original position
        Position origPos = alive.getOrigPosition();
        gameState.getGameBoard().placePlayer(origPos);

        return true;
    }
}
