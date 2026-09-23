public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public TerminalSokobanGame(GameState gameState) {
        super(gameState);
        this.inputEngine = new TerminalInputEngine();
        this.renderingEngine = new TerminalRenderingEngine();
    }

    public TerminalSokobanGame() {
    }

    public void run() {
    }
}
