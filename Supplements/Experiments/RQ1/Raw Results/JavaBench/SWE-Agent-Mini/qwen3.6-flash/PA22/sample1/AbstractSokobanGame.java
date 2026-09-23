public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        throw new NotImplementedException();
    }
}
