class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public TerminalSokobanGame() {
        super();
    }

    public void run() {
        renderingEngine.message("Sokoban game is ready.");
        while (!shouldStop()) {
            renderingEngine.render(state);
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                renderingEngine.message(((Failed) result).getReason());
            }
            if (action instanceof Exit) {
                renderingEngine.message("Game exits.");
                break;
            }
        }
        renderingEngine.render(state);
        if (state.isWin()) {
            renderingEngine.message("You win.");
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
