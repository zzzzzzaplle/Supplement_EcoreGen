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
        
        // Display undo quota
        if (state.getUndoQuota() == -1) {
            renderingEngine.message(StringResources.UNDO_QUOTA_TEMPLATE.replace("%s", StringResources.UNDO_QUOTA_UNLIMITED));
        } else {
            renderingEngine.message(StringResources.UNDO_QUOTA_TEMPLATE.replace("%s", String.valueOf(state.getUndoQuota())));
        }
        
        renderingEngine.render(state);
        
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            
            if (!(action instanceof Exit) && !shouldStop()) {
                renderingEngine.render(state);
            }
        }
        
        if (state.isWin()) {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        } else {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }
}
