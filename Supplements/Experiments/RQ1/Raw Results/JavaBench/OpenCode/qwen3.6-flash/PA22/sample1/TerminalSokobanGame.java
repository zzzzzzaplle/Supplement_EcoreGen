import java.util.*;

class TerminalSokobanGame extends AbstractSokobanGame {

    private InputEngine inputEngine;
    private RenderingEngine renderingEngine;

    public TerminalSokobanGame() {
        this(null, null, null);
    }

    public TerminalSokobanGame(GameState gameState, InputEngine inputEngine, RenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;

        if (gameState.getPlayerIds().size() > 2) {
            throw new IllegalArgumentException("Terminal supports at most 2 players");
        }
    }

    @Override
    protected Action fetchAction() {
        return this.inputEngine.fetchAction();
    }

    @Override
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            this.renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
            setExit();
            return new Success(action);
        }

        if (action instanceof Undo) {
            try {
                this.state.undo();
                this.renderingEngine.render(this.state);
                this.renderingEngine.message("Undo quota: " + getUndoQuotaString());
                return new Success(action);
            } catch (IllegalStateException e) {
                this.renderingEngine.message(e.getMessage());
                return new Failed(e.getMessage());
            }
        }

        if (action instanceof InvalidInput) {
            InvalidInput invalid = (InvalidInput) action;
            this.renderingEngine.message(invalid.getMessage());
            return new Failed(invalid.getMessage());
        }

        if (action instanceof Move) {
            Move move = (Move) action;
            try {
                Set<Integer> playerIds = this.state.getPlayerIds();
                if (playerIds.isEmpty()) {
                    this.renderingEngine.message(StringResources.PLAYER_NOT_FOUND);
                    return new Failed(StringResources.PLAYER_NOT_FOUND);
                }
                int currentPlayer = playerIds.iterator().next();
                Position from = this.state.getPlayerPositionById(currentPlayer);
                Position to = move.nextPosition(from);

                Entity target = this.state.getEntity(to);
                if (target instanceof Wall) {
                    this.renderingEngine.message("Blocked by wall");
                    return new Failed("Blocked by wall");
                }

                if (target instanceof Box) {
                    Box box = (Box) target;
                    if (box.getPlayerId() != currentPlayer) {
                        this.renderingEngine.message("Cannot push another player's box");
                        return new Failed("Cannot push another player's box");
                    }
                    Position boxBehind = nextPosition(to);
                    Entity behind = this.state.getEntity(boxBehind);
                    if (behind instanceof Wall) {
                        this.renderingEngine.message("Cannot push box into wall");
                        return new Failed("Cannot push box into wall");
                    }
                }

                this.state.move(from, to);
                this.renderingEngine.render(this.state);

                if (this.state.isWin()) {
                    this.renderingEngine.message(StringResources.WIN_MESSAGE);
                    setExit();
                }
                return new Success(action);
            } catch (IllegalStateException e) {
                this.renderingEngine.message(e.getMessage());
                return new Failed(e.getMessage());
            }
        }

        return new Failed("Unknown action");
    }

    private Position nextPosition(Position pos) {
        return Position.of(pos.x(), pos.y() + 1);
    }

    private String getUndoQuotaString() {
        if (this.state.undoUnlimited) {
            return StringResources.UNDO_QUOTA_UNLIMITED;
        }
        return String.valueOf(this.state.undoQuota);
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
}
