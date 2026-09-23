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
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
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
        MoveResult move = gameState.getMoveStack().pop();
        if (move instanceof Alive) {
            gameState.getGameBoardController().undoMove(move);
            Alive alive = (Alive) move;
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
            return true;
        }
        return false;
    }
}
