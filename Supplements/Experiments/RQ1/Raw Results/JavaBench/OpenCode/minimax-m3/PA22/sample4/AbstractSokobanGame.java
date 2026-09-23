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
        return isExitSpecified || (state != null && state.isWin());
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Move) {
            return processMove((Move) action);
        } else if (action instanceof Undo) {
            if (state != null) {
                state.undo();
            }
            return new Success(action);
        } else if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action");
    }

    private ActionResult processMove(Move move) {
        if (state == null) {
            return new Failed(move, "No game state");
        }
        Position playerPos = state.getPlayerPositionById(move.getInitiator());
        if (playerPos == null) {
            return new Failed(move, "Player not found");
        }
        Position nextPos = move.nextPosition(playerPos);
        Entity entityAtNext = state.getEntity(nextPos);
        if (entityAtNext instanceof Wall) {
            return new Failed(move, "Wall in the way");
        }
        if (entityAtNext instanceof Player) {
            return new Failed(move, "Another player in the way");
        }
        if (entityAtNext == null) {
            state.move(playerPos, nextPos);
            return new Success(move);
        }
        if (entityAtNext instanceof Box) {
            Box box = (Box) entityAtNext;
            if (box.getPlayerId() != move.getInitiator()) {
                return new Failed(move, "Cannot push other player's box");
            }
            Position boxNext = move.nextPosition(nextPos);
            Entity entityBehindBox = state.getEntity(boxNext);
            if (entityBehindBox == null) {
                state.move(nextPos, boxNext);
                state.checkpoint();
                state.move(playerPos, nextPos);
                return new Success(move);
            }
            return new Failed(move, "Cannot push box");
        }
        return new Failed(move, "Unknown entity");
    }
}
