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
            // Invalid: do nothing
            return result;
        } else if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            // Apply collected ExtraLife effects
            if (alive.getCollectedExtraLives() != null && !alive.getCollectedExtraLives().isEmpty()) {
                gameState.increaseNumLives(alive.getCollectedExtraLives().size());
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
        }
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive) {
            Alive alive = (Alive) move;
            // Reverse finite ExtraLife gains
            if (alive.getCollectedExtraLives() != null && !alive.getCollectedExtraLives().isEmpty()) {
                if (!gameState.hasUnlimitedLives()) {
                    gameState.decreaseNumLives(alive.getCollectedExtraLives().size());
                }
            }
            gameState.getGameBoardController().undoMove(alive);
        }
        return true;
    }
}
