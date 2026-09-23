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

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean getIsExitSpecified() {
        return isExitSpecified;
    }

    public void setIsExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state.isWin();
    }

    protected ActionResult processAction(Action action) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
