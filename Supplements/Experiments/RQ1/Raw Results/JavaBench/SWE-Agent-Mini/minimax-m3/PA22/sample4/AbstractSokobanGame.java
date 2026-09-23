public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
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

    public void setExitSpecified(boolean exitSpecified) {
        this.isExitSpecified = exitSpecified;
    }

    protected boolean shouldStop() {
        return isExitSpecified || state.isWin();
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, StringResources.INVALID_INPUT_MESSAGE);
        }
        if (action instanceof Undo) {
            int quota = state.getUndoQuota();
            if (quota == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (state.getCheckpoints().isEmpty()) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (quota > 0) {
                state.setUndoQuota(quota - 1);
            }
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            if (nextPos == null) {
                return new Failed(action, "Invalid move.");
            }
            Entity target = state.getEntity(nextPos);
            if (target instanceof Wall) {
                return new Failed(action, "Blocked by wall.");
            }
            if (target instanceof Player) {
                return new Failed(action, "Blocked by player.");
            }
            if (target instanceof Box) {
                Box box = (Box) target;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Box belongs to another player.");
                }
                Position beyondPos = move.nextPosition(nextPos);
                if (beyondPos == null) {
                    return new Failed(action, "Out of bounds.");
                }
                Entity beyond = state.getEntity(beyondPos);
                if (beyond instanceof Wall) {
                    return new Failed(action, "Blocked by wall behind box.");
                }
                if (beyond instanceof Player) {
                    return new Failed(action, "Blocked by player behind box.");
                }
                if (beyond instanceof Box) {
                    return new Failed(action, "Blocked by box behind box.");
                }
                // Push the box
                state.move(nextPos, beyondPos);
                state.move(currentPos, nextPos);
                state.getCurrentTransition().add(nextPos, beyondPos);
                state.getCurrentTransition().add(currentPos, nextPos);
                state.checkpoint();
                return new Success(action);
            }
            // Empty space
            state.move(currentPos, nextPos);
            state.getCurrentTransition().add(currentPos, nextPos);
            return new Success(action);
        }
        return new Failed(action, "Unknown action.");
    }
}
