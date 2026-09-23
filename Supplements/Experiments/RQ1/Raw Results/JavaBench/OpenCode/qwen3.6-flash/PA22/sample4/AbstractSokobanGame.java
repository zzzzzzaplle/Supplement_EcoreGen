public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified = false;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    public boolean shouldStop() {
        return isExitSpecified;
    }

    public abstract ActionResult processAction(Action action);
}
