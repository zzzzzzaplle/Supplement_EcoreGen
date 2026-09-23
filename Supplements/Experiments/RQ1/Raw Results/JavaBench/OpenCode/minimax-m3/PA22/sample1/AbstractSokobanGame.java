public abstract class AbstractSokobanGame implements SokobanGame {

    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (state == null) {
            return new Failed(action, "Game state not initialized");
        }
        if (action instanceof Exit) {
            setExitSpecified(true);
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = action.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity destEntity = state.getEntity(nextPos);
            if (destEntity instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            }
            if (destEntity instanceof Player) {
                return new Failed(action, "Blocked by another player");
            }
            if (destEntity instanceof Box) {
                Box box = (Box) destEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push another player's box");
                }
                Position boxNextPos = move.nextPosition(nextPos);
                Entity beyondBox = state.getEntity(boxNextPos);
                if (beyondBox instanceof Wall || beyondBox instanceof Player || beyondBox instanceof Box) {
                    return new Failed(action, "Box is blocked");
                }
                state.move(nextPos, boxNextPos);
                state.move(currentPos, nextPos);
                state.checkpoint();
                return new Success(action);
            }
            state.move(currentPos, nextPos);
            return new Success(action);
        }
        return new Failed(action, "Unknown action");
    }
}
