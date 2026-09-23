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
            // Do not change counters, do not push to stack
            return result;
        }

        gameState.incrementNumMoves();

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            // Apply extra life effects
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(alive);
            return result;
        }

        if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            // Dead is not pushed to MoveStack
            return result;
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult top = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(top);

        if (top instanceof Alive) {
            Alive alive = (Alive) top;
            // Reverse extra life gains
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
        }
        return true;
    }
}
