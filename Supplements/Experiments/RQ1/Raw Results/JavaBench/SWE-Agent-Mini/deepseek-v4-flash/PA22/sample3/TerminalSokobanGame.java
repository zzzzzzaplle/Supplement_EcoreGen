public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            
            if (result instanceof Failed) {
                if (action instanceof InvalidInput) {
                    renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
                } else {
                    Failed failed = (Failed) result;
                    String reason = failed.getReason();
                    if (reason != null) {
                        renderingEngine.message(reason);
                    }
                }
            } else if (result instanceof Success) {
                if (action instanceof Exit) {
                    renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                } else if (action instanceof Undo) {
                    // Check undo quota
                    int quota = state.getUndoQuota();
                    String quotaStr;
                    if (quota == -1) {
                        quotaStr = StringResources.UNDO_QUOTA_UNLIMITED;
                    } else {
                        quotaStr = String.valueOf(quota);
                    }
                    renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, quotaStr));
                }
                renderingEngine.render(state);
                
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                }
            }
        }
    }

    public InputEngine getInputEngine() {
        return inputEngine;
    }

    public void setInputEngine(InputEngine inputEngine) {
        this.inputEngine = inputEngine;
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngine = renderingEngine;
    }
}
