public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    public AbstractSokobanGame() {
    }

    protected boolean shouldStop() {
        return false;
    }

    protected ActionResult processAction(Action action) {
        return null;
    }
}
