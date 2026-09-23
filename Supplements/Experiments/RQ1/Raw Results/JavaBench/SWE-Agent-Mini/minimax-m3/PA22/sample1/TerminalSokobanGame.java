public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        if (renderingEngine != null) {
            renderingEngine.message(StringResources.GAME_READY_MESSAGE);
            renderQuota();
            renderingEngine.render(state);
        }
        while (!shouldStop()) {
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                return;
            }
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
                continue;
            }
            if (action instanceof InvalidInput) {
                renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
                continue;
            }
            if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                return;
            }
            if (action instanceof Undo) {
                renderQuota();
            }
            renderingEngine.render(state);
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }

    private void renderQuota() {
        if (renderingEngine == null) return;
        if (state.getUndoQuota() == -1) {
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, StringResources.UNDO_QUOTA_UNLIMITED));
        } else {
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, state.getUndoQuota()));
        }
    }

    public InputEngine getInputEngine() {
        return this.inputEngine;
    }

    public void setInputEngine(InputEngine inputEngine) {
        this.inputEngine = inputEngine;
    }

    public RenderingEngine getRenderingEngine() {
        return this.renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }
}
