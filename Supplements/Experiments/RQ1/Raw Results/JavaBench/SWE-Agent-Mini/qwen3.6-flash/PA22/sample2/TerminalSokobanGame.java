
import java.util.*;

/**
 * Terminal-based Sokoban game implementation.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
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

            // Display undo quota information
            Optional<Integer> undoOpt = state.getUndoLimit();
            int displayValue = 0;
            if (undoOpt.isPresent()) {
                int limit = undoOpt.get();
                if (limit < 0) {
                    renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, StringResources.UNDO_QUOTA_UNLIMITED));
                    continue;
                }
                displayValue = state.getUndoQuota();
            }
            if (undoOpt.isPresent() && displayValue >= 0 && !shouldStop()) {
                renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, displayValue));
            }
        }

        if (isExitSpecified) {
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        } else {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        }
    }
}
