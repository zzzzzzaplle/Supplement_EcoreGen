public class TerminalSokobanGame extends AbstractSokobanGame {
    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        while (!shouldStop()) {
            renderingEngine.render(state);
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (result instanceof Success) {
                Action successAction = ((Success) result).action;
                if (successAction instanceof Exit) {
                    renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                    break;
                }
            }
        }
    }
}
