import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private GameState gameState;

    public GameController(GameState gameState) {
        this.gameState = Objects.requireNonNull(gameState);
    }

    public GameState getGameState() {
        return gameState;
    }

    public MoveResult processMove(Direction direction) {
        Player player = gameState.getPlayer();
        if (player == null) {
            return null;
        }

        EntityCell playerCell = player.getOwner();
        if (playerCell == null) {
            return null;
        }

        Position currentPosition = playerCell.getPosition();
        MoveResult result = gameState.getGameBoardController().makeMove(direction);
        return result;
    }

    public boolean processUndo() {
        return gameState.getGameBoardController().undoMove();
    }
}
