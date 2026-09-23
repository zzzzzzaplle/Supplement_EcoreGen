/**
 * Main game controller that processes moves and undo operations.
 */
public class GameController {

    private GameState gameState;

    public GameController() {
    }

    public GameController(final GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(final GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Processes a move in the given direction.
     *
     * @param direction The direction to move.
     * @return The result of the move.
     */
    public MoveResult processMove(final Direction direction) {
        final GameBoardController controller = gameState.getGameBoardController();
        final MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves don't change counters, not pushed to stack
            return result;
        }

        if (result instanceof Alive) {
            final Alive alive = (Alive) result;
            gameState.incrementNumMoves();

            // Apply extra life effects
            for (final Position extraLifePos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            // Push to stack
            gameState.getMoveStack().push(alive);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            // Dead moves are not pushed to stack
        }

        return result;
    }

    /**
     * Processes an undo operation.
     *
     * @return true if an undo was performed, false otherwise.
     */
    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        final MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive) {
            final GameBoardController controller = gameState.getGameBoardController();
            final Alive alive = (Alive) move;

            // Reverse extra life gains
            for (final Position extraLifePos : alive.getCollectedExtraLives()) {
                gameState.decreaseNumLives(1);
            }

            controller.undoMove(alive);
            return true;
        }

        return false;
    }
}
