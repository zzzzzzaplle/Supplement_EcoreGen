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
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Invalid) {
            return result;
        }

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            int extraLives = alive.getCollectedExtraLives().size();
            if (extraLives > 0) {
                gameState.increaseNumLives(extraLives);
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
        MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }

        MoveResult last = stack.pop();
        if (!(last instanceof Alive)) {
            return false;
        }

        Alive alive = (Alive) last;
        gameState.getGameBoardController().undoMove(alive);

        int extraLives = alive.getCollectedExtraLives().size();
        if (extraLives > 0 && !gameState.hasUnlimitedLives()) {
            gameState.decreaseNumLives(extraLives);
        }

        return true;
    }
}
