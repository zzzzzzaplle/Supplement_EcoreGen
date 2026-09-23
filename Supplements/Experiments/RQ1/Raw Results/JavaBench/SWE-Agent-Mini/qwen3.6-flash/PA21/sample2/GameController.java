import java.util.Objects;

public class GameController {
    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        if (gameState.hasLost()) {
            return new Invalid(gameState.getGameBoard().getPlayer().getOwner() != null ?
                    gameState.getGameBoard().getPlayer().getOwner().getPosition() : new Position(0, 0));
        }

        MoveResult result = gameState.getGameBoardController().makeMove(direction);

        if (result instanceof Invalid) {
            // Invalid moves do not change counters and are not pushed to the MoveStack
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;

            // Increment numMoves
            gameState.incrementNumMoves();

            // Apply collected ExtraLife effects
            for (Position extraLifePos : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }

            // Push to MoveStack
            gameState.getMoveStack().push(alive);

            return result;
        }

        if (result instanceof Dead) {
            // Dead moves increment numMoves and numDeaths
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();

            // Decrease finite lives by one
            gameState.decrementNumLives();

            // Not pushed to MoveStack
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        MoveStack stack = gameState.getMoveStack();

        if (stack.isEmpty()) {
            return false;
        }

        // Must undo an Alive move
        MoveResult lastMove = stack.peek();
        if (!(lastMove instanceof Alive)) {
            return false;
        }

        Alive alive = (Alive) stack.pop();

        // Undo in the controller
        gameState.getGameBoardController().undoMove(alive);

        // Reverse finite ExtraLife gains
        if (!gameState.hasUnlimitedLives()) {
            gameState.decreaseNumLives(alive.getCollectedExtraLives().size());
        }

        // Undo does not decrement numMoves; undo count is represented by MoveStack.popCount

        return true;
    }
}
