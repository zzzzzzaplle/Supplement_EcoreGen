import java.util.Objects;

public class GameController {

    private GameState gameState;

    public GameController() {
    }

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public MoveResult processMove(Direction direction) {
        Objects.requireNonNull(direction);
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            gameState.incrementNumMoves();
            int extraLivesCollected = alive.getCollectedExtraLives().size();
            if (extraLivesCollected > 0) {
                gameState.increaseNumLives(extraLivesCollected);
            }
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumMoves();
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }
        return result;
    }

    public boolean processUndo() {
        MoveStack stack = gameState.getMoveStack();
        if (stack.isEmpty()) {
            return false;
        }
        MoveResult prev = stack.pop();
        gameState.getGameBoardController().undoMove(prev);
        if (prev instanceof Alive) {
            Alive alive = (Alive) prev;
            int extraLivesCollected = alive.getCollectedExtraLives().size();
            if (extraLivesCollected > 0) {
                gameState.decreaseNumLives(extraLivesCollected);
            }
        }
        return true;
    }
}
