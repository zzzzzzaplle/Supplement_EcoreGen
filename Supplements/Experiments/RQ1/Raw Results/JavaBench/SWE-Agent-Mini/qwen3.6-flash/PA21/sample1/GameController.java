import java.util.Objects;

/**
 * Main game controller that processes moves and undo operations.
 */
public class GameController {
    private GameState gameState;

    /**
     * Creates a new GameController for the specified game state.
     *
     * @param gameState The game state.
     */
    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a move in the specified direction.
     *
     * @param direction The direction to move.
     * @return The move result.
     */
    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed to the MoveStack
            return result;
        } else if (result instanceof Alive) {
            // Alive moves increment numMoves and push to MoveStack
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;

            // Apply collected extra lives - each extra life gives +1 life
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths, decrease lives by one
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            // Dead moves are NOT pushed to the MoveStack
        }

        return result;
    }

    /**
     * Processes an undo operation.
     *
     * @return true if an undo was performed, false otherwise.
     */
    public boolean processUndo() {
        MoveStack moveStack = gameState.getMoveStack();

        if (moveStack.isEmpty()) {
            return false;
        }

        // Only Alive moves are undoable
        MoveResult move = moveStack.pop();

        if (move instanceof Alive) {
            gameState.setNumUndos(gameState.getNumUndos() + 1);

            Alive alive = (Alive) move;
            GameBoard gameBoard = gameState.getGameBoard();
            Player player = gameBoard.getPlayer();
            Position origPosition = alive.getOrigPosition();

            // Restore player position
            // Remove player from current position
            Position currentPos = move.getNewPosition();
            EntityCell currentEntityCell = gameBoard.getEntityCell(currentPos.getRow(), currentPos.getCol());
            currentEntityCell.setEntity(null);

            // Place player at original position
            EntityCell origEntityCell = gameBoard.getEntityCell(origPosition.getRow(), origPosition.getCol());
            origEntityCell.setEntity(player);

            // Restore collected gems
            for (Position gemPos : alive.getCollectedGems()) {
                EntityCell gemCell = gameBoard.getEntityCell(gemPos.getRow(), gemPos.getCol());
                gemCell.setEntity(new Gem());
            }

            // Revert collected extra lives - reverse finite extra life gains
            for (Position extraLifePos : alive.getCollectedExtraLives()) {
                // Restore the extra life to the board
                EntityCell lifeCell = gameBoard.getEntityCell(extraLifePos.getRow(), extraLifePos.getCol());
                lifeCell.setEntity(new ExtraLife());

                // Reverse the life gain
                if (gameState.getNumLivesInternal() >= 0) {
                    gameState.decreaseNumLives(1);
                }
            }
        }

        // For other move types, we still restore player position but don't revert lives
        return true;
    }

    /**
     * Gets the game state.
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
        this.gameState = gameState;
    }
}
