import java.util.Set;

public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() { return isExitSpecified; }

    protected abstract ActionResult processAction(Action action);

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public boolean getIsExitSpecified() { return isExitSpecified; }
    public void setIsExitSpecified(boolean isExitSpecified) { this.isExitSpecified = isExitSpecified; }
}
