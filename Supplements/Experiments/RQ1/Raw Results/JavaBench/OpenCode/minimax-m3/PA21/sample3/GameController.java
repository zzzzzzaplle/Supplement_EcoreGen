import java.util.Objects;

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
        Objects.requireNonNull(direction);
        Objects.requireNonNull(gameState);
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        if (result instanceof Invalid) {
            return result;
        }
        gameState.incrementNumMoves();
        if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            if (!gameState.hasUnlimitedLives()) {
                gameState.decrementNumLives();
            }
        } else if (result instanceof Alive) {
            Alive alive = (Alive) result;
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.increaseNumLives(1);
            }
            gameState.getMoveStack().push(result);
        }
        return result;
    }

    public boolean processUndo() {
        Objects.requireNonNull(gameState);
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }
        MoveResult m = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(m);
        if (m instanceof Alive) {
            Alive alive = (Alive) m;
            for (int i = 0; i < alive.getCollectedExtraLives().size(); i++) {
                gameState.decreaseNumLives(1);
            }
        }
        return true;
    }
}
