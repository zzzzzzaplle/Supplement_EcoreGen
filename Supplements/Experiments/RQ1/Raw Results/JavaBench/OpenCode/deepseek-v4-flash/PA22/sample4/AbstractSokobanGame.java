public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity targetEntity = state.getEntity(nextPos);
            if (targetEntity == null || targetEntity instanceof Wall) {
                return new Failed(action, "Cannot move there");
            }
            if (targetEntity instanceof Player) {
                return new Failed(action, "Cannot move there");
            }
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot move there");
                }
                Position behindBox = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindBox);
                if (behindEntity == null || behindEntity instanceof Wall || behindEntity instanceof Box || behindEntity instanceof Player) {
                    return new Failed(action, "Cannot move there");
                }
                state.move(currentPos, nextPos);
                return new Success(action);
            }
            if (targetEntity instanceof Empty) {
                state.move(currentPos, nextPos);
                return new Success(action);
            }
            return new Failed(action, "Cannot move there");
        }
        return new Failed(action, "Unknown action");
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
}
