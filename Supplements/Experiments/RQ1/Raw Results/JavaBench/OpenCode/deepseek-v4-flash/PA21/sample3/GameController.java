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
            gameState.incrementNumMoves();
            Alive alive = (Alive) result;
            for (Position ignored : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
            return result;
        }

        if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        MoveResult top = gameState.getMoveStack().peek();
        if (!(top instanceof Alive)) {
            return false;
        }

        MoveResult move = gameState.getMoveStack().pop();
        Alive alive = (Alive) move;

        GameBoardController controller = gameState.getGameBoardController();
        controller.undoMove(alive);

        for (Position ignored : alive.getCollectedExtraLives()) {
            gameState.decreaseNumLives(1);
        }

        return true;
    }
}
