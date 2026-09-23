import java.util.Optional;

public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);

        while (!shouldStop()) {
            renderingEngine.render(state);

            // Display undo quota
            Optional<Integer> undoLimit = Optional.of(state.getUndoQuota());
            String undoQuotaStr;
            if (undoLimit.orElse(-1) == -1) {
                undoQuotaStr = StringResources.UNDO_QUOTA_UNLIMITED;
            } else if (undoLimit.orElse(0) == 0) {
                undoQuotaStr = StringResources.UNDO_QUOTA_RUN_OUT;
            } else {
                undoQuotaStr = String.valueOf(undoLimit.get());
            }
            renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, undoQuotaStr));

            Action action = inputEngine.fetchAction();
            ActionResult result = processAction(action);

            if (result instanceof Success) {
                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
            } else if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            }
        }
        renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
    }

    @Override
    protected boolean shouldStop() {
        return isExitSpecified;
    }

    @Override
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }

        if (action instanceof InvalidInput) {
            InvalidInput invalid = (InvalidInput) action;
            return new Failed(action, invalid.getMessage());
        }

        if (action instanceof Undo) {
            try {
                state.undo();
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
            return new Success(action);
        }

        if (action instanceof Move) {
            Move move = (Move) action;
            int player = action.getInitiator();
            Position currentPos = state.getPlayerPositionById(player);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            try {
                Position nextPos = move.nextPosition(currentPos);
                state.move(currentPos, nextPos);
                return new Success(action);
            } catch (RuntimeException e) {
                return new Failed(action, e.getMessage());
            }
        }

        return new Failed(action, "Unknown action type");
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
