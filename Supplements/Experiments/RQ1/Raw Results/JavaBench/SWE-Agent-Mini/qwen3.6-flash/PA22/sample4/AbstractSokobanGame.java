import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed("Invalid input.");
        }
        if (action instanceof Undo) {
            if (!state.canUndo()) {
                return new Failed("No more undo available.");
            }
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = action.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            if (currentPos == null) {
                return new Failed("Player not found.");
            }
            Position nextPos = move.nextPosition(currentPos);
            try {
                state.move(currentPos, nextPos);
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(e.getMessage());
            }
        }
        return new Failed("Unknown action type.");
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
