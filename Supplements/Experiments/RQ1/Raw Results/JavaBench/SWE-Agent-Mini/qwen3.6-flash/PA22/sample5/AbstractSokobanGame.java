import java.util.Optional;

public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected abstract boolean shouldStop();

    protected abstract ActionResult processAction(Action action);

    @Override
    public void run() {
        // To be implemented by subclasses
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
