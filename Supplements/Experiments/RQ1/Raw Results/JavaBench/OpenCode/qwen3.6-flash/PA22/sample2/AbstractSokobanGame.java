/**
 * Abstract base class for Sokoban games.
 */
public abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this(null);
    }

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, StringResources.INVALID_INPUT_MESSAGE);
        } else if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            Position playerPos = state.getPlayerPositionById(action.getInitiator());
            if (playerPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(playerPos);
            state.move(playerPos, nextPos);
            state.checkpoint();
            return new Success(action);
        }
        return new Failed(action, "Unknown action");
    }
}
