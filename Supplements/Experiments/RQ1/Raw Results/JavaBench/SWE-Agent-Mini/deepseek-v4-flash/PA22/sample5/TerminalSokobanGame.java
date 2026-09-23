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
        
        while (!shouldStop()) {
            renderingEngine.render(state);
            
            // Show undo quota
            int undoQuota = state.getUndoQuota();
            String quotaText;
            if (undoQuota == -1) {
                quotaText = StringResources.UNDO_QUOTA_UNLIMITED;
            } else {
                quotaText = String.valueOf(undoQuota);
            }
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, quotaText));
            
            Action action = inputEngine.fetchAction();
            
            if (action instanceof Exit) {
                setExitSpecified(true);
                break;
            }
            
            ActionResult result = processAction(action);
            
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            }
            
            if (state.isWin()) {
                renderingEngine.render(state);
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
        
        if (isExitSpecified()) {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
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
