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
        } else if (action instanceof Undo) {
            int undoQuota = state.getUndoQuota();
            if (undoQuota == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            // Check if there is a checkpoint to undo
            if (state.getHistory().isEmpty()) {
                return new Failed(action, "Nothing to undo.");
            }
            state.undo();
            // Consume undo quota only when there was a checkpoint to undo (finite quota, not unlimited)
            if (undoQuota > 0) {
                state.setUndoQuota(undoQuota - 1);
            }
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position playerPos = state.getPlayerPositionById(initiator);
            if (playerPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(playerPos);
            Entity targetEntity = state.getEntity(nextPos);
            
            if (targetEntity instanceof Wall) {
                return new Failed(action, "Cannot move into a wall.");
            }
            if (targetEntity instanceof Player) {
                return new Failed(action, "Cannot move into another player.");
            }
            
            boolean pushedBox = false;
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                Position behindBox = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindBox);
                if (behindEntity instanceof Wall || behindEntity instanceof Player || behindEntity instanceof Box) {
                    return new Failed(action, "Cannot push box into obstacle.");
                }
                // Move the box
                state.move(nextPos, behindBox);
                pushedBox = true;
            }
            // Move the player
            state.move(playerPos, nextPos);
            
            // Record checkpoint if a box was pushed
            if (pushedBox) {
                state.checkpoint();
            }
            
            return new Success(action);
        } else if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        return new Failed(action, "Unknown action.");
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
