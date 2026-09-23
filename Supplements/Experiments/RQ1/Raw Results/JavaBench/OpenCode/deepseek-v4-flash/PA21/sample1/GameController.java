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

        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            return result;
        }

        if (result instanceof Alive) {
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult result = gameState.getMoveStack().pop();
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            GameBoardController controller = gameState.getGameBoardController();
            controller.undoMove(result);
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.decreaseNumLives(1);
            }
            return true;
        }
        return false;
    }
}
