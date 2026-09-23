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
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(alive);
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
        MoveStack moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }
        MoveResult top = moveStack.peek();
        if (!(top instanceof Alive)) {
            return false;
        }
        Alive alive = (Alive) moveStack.pop();
        GameBoardController controller = gameState.getGameBoardController();
        controller.undoMove(alive);
        for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
            gameState.decreaseNumLives(1);
        }
        return true;
    }
}
