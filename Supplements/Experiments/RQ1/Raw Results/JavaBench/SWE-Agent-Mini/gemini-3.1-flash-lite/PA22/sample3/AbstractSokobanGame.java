public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {}

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected void setExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }

    protected abstract ActionResult processAction(Action action);

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public boolean isExitSpecified() { return isExitSpecified; }
}
