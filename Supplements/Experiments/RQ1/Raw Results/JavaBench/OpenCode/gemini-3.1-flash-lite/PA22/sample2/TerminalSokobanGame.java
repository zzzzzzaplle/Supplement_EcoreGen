public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {}

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public InputEngine getInputEngine() { return inputEngine; }
    public void setInputEngine(InputEngine inputEngine) { this.inputEngine = inputEngine; }
    public RenderingEngine getRenderingEngine() { return renderingEngine; }
    public void setRenderingEngine(RenderingEngine renderingEngine) { this.renderingEngine = renderingEngine; }

    public void run() {}
    protected boolean shouldStop() { return false; }
    protected ActionResult processAction(Action action) { return null; }
}
