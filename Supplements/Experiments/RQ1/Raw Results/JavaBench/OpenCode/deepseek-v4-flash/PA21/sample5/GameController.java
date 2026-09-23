public class GameController {
    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        GameBoardController boardController = gameState.getGameBoardController();
        MoveResult result = boardController.makeMove(direction);

        if (result instanceof Invalid) {
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

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            for (Position p : alive.getCollectedExtraLives()) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
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

        GameBoardController boardController = gameState.getGameBoardController();
        boardController.undoMove(alive);

        for (Position p : alive.getCollectedExtraLives()) {
            gameState.decreaseNumLives(1);
        }

        return true;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
