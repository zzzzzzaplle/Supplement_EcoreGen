import java.util.Scanner;

public class TerminalSokobanGame extends AbstractSokobanGame {
    private final InputEngine inputEngine;
    private final RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    public void run() {
        renderingEngine.render(state);
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            
            renderingEngine.render(state);
            
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            }
            
            if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
            
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
        }
    }
}
