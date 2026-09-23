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

    @Override
    public void run() {
        renderingEngine.render(state);
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);

        while (!shouldStop()) {
            Action action = inputEngine.fetchAction();

            if (action instanceof Exit) {
                setExitSpecified(true);
                renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
                break;
            }

            if (action instanceof InvalidInput) {
                renderingEngine.message(((InvalidInput) action).getMessage());
                continue;
            }

            if (action instanceof Undo) {
                int initiator = action.getInitiator();
                if (initiator > 1) {
                    continue;
                }
                int quota = state.getUndoQuota();
                if (quota == 0) {
                    renderingEngine.message(StringResources.UNDO_QUOTA_RUN_OUT);
                } else {
                    state.undo();
                    if (quota > 0) {
                        String quotaMsg = quota == -1
                            ? StringResources.UNDO_QUOTA_UNLIMITED
                            : String.valueOf(quota);
                        renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, quotaMsg));
                    } else {
                        renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, StringResources.UNDO_QUOTA_UNLIMITED));
                    }
                    renderingEngine.render(state);
                }
                continue;
            }

            if (action instanceof Move) {
                int initiator = action.getInitiator();
                if (initiator > 1) {
                    continue;
                }
                Move move = (Move) action;
                Position currentPos = state.getPlayerPositionById(initiator);
                if (currentPos == null) {
                    renderingEngine.message(StringResources.PLAYER_NOT_FOUND);
                    continue;
                }
                Position nextPos = move.nextPosition(currentPos);
                Entity targetEntity = state.getEntity(nextPos);

                if (targetEntity instanceof Wall) {
                    renderingEngine.message("Cannot move into a wall.");
                    continue;
                }

                if (targetEntity instanceof Player) {
                    renderingEngine.message("Cannot move into another player.");
                    continue;
                }

                if (targetEntity instanceof Box) {
                    Box box = (Box) targetEntity;
                    if (box.getPlayerId() != initiator) {
                        renderingEngine.message("Cannot push another player's box.");
                        continue;
                    }
                    Position behindBox = move.nextPosition(nextPos);
                    Entity behindEntity = state.getEntity(behindBox);
                    if (!(behindEntity == null || behindEntity instanceof Empty)) {
                        renderingEngine.message("Cannot push box into a wall or other entity.");
                        continue;
                    }
                    state.move(currentPos, nextPos);
                    state.checkpoint();
                } else {
                    state.move(currentPos, nextPos);
                }

                renderingEngine.render(state);

                if (state.isWin()) {
                    renderingEngine.message(StringResources.WIN_MESSAGE);
                    break;
                }
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
