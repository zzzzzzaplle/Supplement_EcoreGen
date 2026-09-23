public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {}

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public boolean getIsExitSpecified() { return isExitSpecified; }
    public void setIsExitSpecified(boolean isExitSpecified) { this.isExitSpecified = isExitSpecified; }

    protected abstract boolean shouldStop();
    protected abstract ActionResult processAction(Action action);
}
