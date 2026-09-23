public class GameController {
    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        if (result instanceof Invalid) {
            // Invalid moves do not change counters
            return result;
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            return result;
        } else if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            // Apply collected ExtraLife effects
            if (!gameState.hasUnlimitedLives()) {
                for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                    gameState.increaseNumLives(1);
                }
            }
            gameState.getMoveStack().push(alive);
            return result;
        }
        return result;
    }

    public boolean processUndo() {
        MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }
        MoveResult prev = stack.pop();
        gameState.getGameBoardController().undoMove(prev);
        if (prev instanceof Alive) {
            Alive alive = (Alive) prev;
            // Reverse finite extra life gains
            if (!gameState.hasUnlimitedLives()) {
                for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                    gameState.decreaseNumLives(1);
                }
            }
        }
        return true;
    }
}
