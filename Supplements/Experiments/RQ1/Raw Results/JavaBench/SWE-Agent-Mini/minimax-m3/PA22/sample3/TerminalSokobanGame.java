import java.util.Set;

public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = new TerminalInputEngine(System.in);
        this.renderingEngine = new TerminalRenderingEngine(System.out);
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
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

    @Override
    public void run() {
        this.renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        this.renderingEngine.render(this.state);
        while (!shouldStop()) {
            Action action = this.inputEngine.fetchAction();
            ActionResult result = this.processAction(action);
            if (result instanceof Failed) {
                this.renderingEngine.message(((Failed) result).getReason());
            } else if (action instanceof InvalidInput) {
                this.renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
            } else if (action instanceof Exit) {
                this.renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                this.setIsExitSpecified(true);
                break;
            }
            this.renderingEngine.render(this.state);
            if (this.state.isWin()) {
                this.renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
    }
}
