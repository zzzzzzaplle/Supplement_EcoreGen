public class TerminalSokobanGame extends AbstractSokobanGame {
    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        super();
        this.inputEngine = null;
        this.renderingEngine = null;
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
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

    @Override
    public void run() {
        if (inputEngine == null || renderingEngine == null || state == null) {
            return;
        }
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);
        renderingEngine.render(state);
        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();
            if (action == null) {
                continue;
            }
            if (action instanceof Exit) {
                setIsExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }
            if (action instanceof InvalidInput) {
                renderingEngine.message(((InvalidInput) action).getMessage());
                continue;
            }
            if (action instanceof Undo) {
                state.undo();
                renderingEngine.render(state);
                continue;
            }
            if (action instanceof Move) {
                Move move = (Move) action;
                int pid = move.getInitiator();
                Position from = state.getPlayerPositionById(pid);
                if (from == null) {
                    renderingEngine.message(StringResources.PLAYER_NOT_FOUND);
                    continue;
                }
                Position to = move.nextPosition(from);
                Entity target = state.getEntity(to);
                if (target instanceof Empty) {
                    state.move(from, to);
                } else if (target instanceof Box) {
                    Box box = (Box) target;
                    if (box.getPlayerId() == pid) {
                        Position beyond = move.nextPosition(to);
                        Entity beyondEntity = state.getEntity(beyond);
                        if (beyondEntity instanceof Empty) {
                            state.checkpoint();
                            state.move(to, beyond);
                            state.move(from, to);
                        } else {
                            renderingEngine.message("Cannot push box.");
                            continue;
                        }
                    } else {
                        renderingEngine.message("Cannot push box.");
                        continue;
                    }
                } else {
                    renderingEngine.message("Move blocked.");
                    continue;
                }
                if (state.isWin()) {
                    renderingEngine.render(state);
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
                renderingEngine.render(state);
            }
        }
    }
}
