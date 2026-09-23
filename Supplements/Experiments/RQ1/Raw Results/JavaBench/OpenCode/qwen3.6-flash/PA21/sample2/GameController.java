import java.util.Objects;

public class GameController {
    private final GameState gameState;

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public MoveResult processMove(Direction direction) {
        GameBoardController controller = gameState.getGameBoardController();
        MoveResult result = controller.makeMove(direction);

        if (result instanceof Alive) {
            Alive alive = (Alive) result;
            for (Position gem : alive.getCollectedGems()) {
                gameState.collectGem(gem);
            }
            for (Position extraLife : alive.getCollectedExtraLives()) {
                gameState.collectExtraLife(extraLife);
            }
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(result);
        } else if (result instanceof Dead) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            gameState.incrementNumMoves();
        }

        return result;
    }

    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        MoveResult result = gameState.getMoveStack().pop();
        gameState.undoMove(result);
        return true;
    }
}
