public class GameController {
    private GameState gameState;

    public GameController() {
        this.gameState = new GameState();
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
            // Do not change counters, not pushed to stack
            return result;
        }
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            // Apply extra life effects
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            // Dead moves are not pushed
        }
        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult move = gameState.getMoveStack().pop();
        if (!(move instanceof Alive)) {
            return false;
        }
        Alive alive = (Alive) move;
        // Reverse finite extra life gains
        if (!gameState.hasUnlimitedLives()) {
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
        }
        // Restore player position and gems/extra lives
        gameState.getGameBoardController().undoMove(alive);
        return true;
    }
}
