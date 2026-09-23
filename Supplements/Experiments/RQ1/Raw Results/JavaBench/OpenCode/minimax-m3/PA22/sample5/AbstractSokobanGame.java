public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public abstract void run();

    protected abstract boolean shouldStop();

    protected abstract ActionResult processAction(Action action);

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

    public boolean isExitSpecified() {
        return isExitSpecified;
    }
}
