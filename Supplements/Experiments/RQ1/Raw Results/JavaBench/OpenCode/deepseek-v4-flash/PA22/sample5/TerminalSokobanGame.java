public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public TerminalSokobanGame() {
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            } else {
                if (action instanceof Exit) {
                    renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                    break;
                }
                if (action instanceof Undo) {
                    String quotaStr;
                    if (state.getUndoQuota() == -1) {
                        quotaStr = StringResources.UNDO_QUOTA_UNLIMITED;
                    } else {
                        quotaStr = String.valueOf(state.getUndoQuota());
                    }
                    renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, quotaStr));
                }
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            }
            renderingEngine.render(state);
        }
    }
}
