public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = null;
        this.renderingEngine = null;
    }

    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
    }

    @Override
    public void run() {
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action instanceof Exit) {
                setIsExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
            if (action instanceof InvalidInput) {
                renderingEngine.message(StringResources.INVALID_INPUT_MESSAGE);
                continue;
            }
            ActionResult result = processAction(action);
            if (result instanceof Failed) {
                Failed failed = (Failed) result;
                renderingEngine.message(failed.getReason());
            }
            renderingEngine.render(state);
            if (state.isWin()) {
                renderingEngine.message(StringResources.WIN_MESSAGE);
                break;
            }
            if (action instanceof Undo) {
                if (state.getUndoQuota() == 0) {
                    renderingEngine.message(StringResources.UNDO_QUOTA_RUN_OUT);
                }
            }
        }
    }

    @Override
    protected boolean shouldStop() {
        return getIsExitSpecified() || state.isWin();
    }

    @Override
    protected ActionResult processAction(Action action) {
        if (action instanceof Move) {
            Move move = (Move) action;
            int pid = move.getInitiator();
            Position current = state.getPlayerPositionById(pid);
            if (current == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position next = move.nextPosition(current);
            Entity nextEntity = state.getEntity(next);
            if (nextEntity instanceof Wall) {
                return new Failed(action, "Wall blocks movement.");
            }
            if (nextEntity instanceof Player) {
                return new Failed(action, "Another player blocks movement.");
            }
            if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != pid) {
                    return new Failed(action, "Box does not belong to you.");
                }
                Position beyond = move.nextPosition(next);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity == null || beyondEntity instanceof Empty) {
                    GameStateTransition t = new GameStateTransition();
                    t.add(current, beyond);
                    t.add(next, current);
                    state.move(current, next);
                    state.move(next, beyond);
                    state.checkpoint();
                    state.getHistory().push(t);
                    return new Success(action);
                }
                return new Failed(action, "Box cannot be pushed.");
            }
            state.move(current, next);
            return new Success(action);
        }
        if (action instanceof Undo) {
            int quota = state.getUndoQuota();
            if (quota == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            if (quota > 0) {
                state.setUndoQuota(quota - 1);
            }
            return new Success(action);
        }
        return new Failed(action, "Unknown action.");
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
