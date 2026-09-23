import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, StringResources.INVALID_INPUT_MESSAGE);
        } else if (action instanceof Undo) {
            if (!state.canUndo()) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            Player player = state.getPlayerById(action.getInitiator());
            if (player == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position currentPosition = state.getPlayerPositionById(action.getInitiator());
            Position nextPosition = move.nextPosition(currentPosition);
            return state.tryMove(currentPosition, nextPosition);
        }
        return new Failed(action, "Unknown action");
    }
}
