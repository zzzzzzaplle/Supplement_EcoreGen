public class GameController {
    private GameState gameState;

    public GameController() {
        this.gameState = null;
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
        if (gameState == null) {
            return new Invalid();
        }

        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            return result;
        }

        gameState.incrementNumMoves();

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            if (alive.getCollectedExtraLives() != null) {
                for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                    gameState.increaseNumLives(1);
                }
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            gameState.incrementNumDeaths();
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState == null) {
            return false;
        }

        MoveStack stack = gameState.getMoveStack();
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        MoveResult prev = stack.pop();
        if (!(prev instanceof Alive)) {
            return false;
        }

        Alive alive = (Alive) prev;
        gameState.getGameBoardController().undoMove(prev);

        if (alive.getCollectedExtraLives() != null && !gameState.hasUnlimitedLives()) {
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
        }

        return true;
    }
}
