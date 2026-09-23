import java.util.Optional;

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

    public void run() {
        String undoLimitStr;
        Optional<Integer> undoLimit = state.getUndoLimit();
        if (undoLimit.isPresent()) {
            int limit = undoLimit.get();
            if (limit == -1) {
                undoLimitStr = StringResources.UNDO_QUOTA_UNLIMITED;
            } else {
                undoLimitStr = String.valueOf(limit);
            }
        } else {
            undoLimitStr = StringResources.UNDO_QUOTA_UNLIMITED;
        }
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, undoLimitStr));
        renderingEngine.render(state);

        while (!shouldStop() && !state.isWin()) {
            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                if (failed.getReason() != null) {
                    renderingEngine.message(failed.getReason());
                } else {
                    renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
                }
            } else if (action instanceof Exit) {
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                return;
            } else if (action instanceof InvalidInput) {
                InvalidInput invalidInput = (InvalidInput) action;
                renderingEngine.message(invalidInput.getMessage());
            }
            renderingEngine.render(state);
        }

        if (state.isWin()) {
            renderingEngine.message(StringResources.WIN_MESSAGE);
        }
    }
}
