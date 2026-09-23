public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = null;
        this.renderingEngine = null;
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
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

    @Override
    public void run() {
        if (renderingEngine != null) {
            renderingEngine.message(StringResources.GAME_READY_MESSAGE);
            String quotaText = (state.getUndoLimit() == -1)
                    ? StringResources.UNDO_QUOTA_UNLIMITED
                    : String.valueOf(state.getUndoLimit());
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, quotaText));
        }
        while (!shouldStop()) {
            if (renderingEngine != null) {
                renderingEngine.render(state);
            }
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                if (renderingEngine != null) {
                    renderingEngine.message(((Failed) result).getReason());
                }
            } else if (result instanceof Success) {
                if (action instanceof Exit) {
                    if (renderingEngine != null) {
                        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                    }
                    break;
                }
            }
            if (state.isWin()) {
                if (renderingEngine != null) {
                    renderingEngine.render(state);
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                }
                break;
            }
        }
    }
}
