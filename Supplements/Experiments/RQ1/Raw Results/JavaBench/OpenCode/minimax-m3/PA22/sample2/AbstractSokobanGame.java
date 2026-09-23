public abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof Undo) {
            if (state != null) state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            if (state == null) return new Failed();
            Position from = state.getPlayerPositionById(initiator);
            if (from == null) return new Failed();
            Position to = move.nextPosition(from);
            Entity target = state.getEntity(to);
            if (target == null || target instanceof Empty) {
                state.move(from, to);
                return new Success(action);
            }
            if (target instanceof Box) {
                state.move(from, to);
                return new Success(action);
            }
            return new Failed();
        }
        if (action instanceof InvalidInput) {
            return new Failed();
        }
        return new Failed();
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}
