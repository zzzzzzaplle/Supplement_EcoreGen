public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return false;
    }

    protected ActionResult processAction(Action action) {
        return null;
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
        this.isExitSpecified = exitSpecified;
    }
}
