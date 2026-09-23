public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {}

    protected AbstractSokobanGame(GameState state) {
        this.state = state;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        return new Failed(action, "Not implemented");
    }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public boolean getIsExitSpecified() { return isExitSpecified; }
    public void setIsExitSpecified(boolean isExitSpecified) { this.isExitSpecified = isExitSpecified; }
}
