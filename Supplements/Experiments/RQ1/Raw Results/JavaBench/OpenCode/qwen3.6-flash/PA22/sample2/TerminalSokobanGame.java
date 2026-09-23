import java.util.Set;

/**
 * Terminal-based Sokoban game implementation.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super(null);
        this.inputEngine = null;
        this.renderingEngine = null;
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);

        while (!shouldStop()) {
            renderingEngine.render(state);

            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);

            if (result instanceof Success) {
                // Success - continue game
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            } else if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            } else if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
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
