public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    protected boolean shouldStop() {
        return isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            int quota = state.getUndoQuota();
            if (quota == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (state.getCheckpointStack().isEmpty()) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = action.getInitiator();
            Position current = state.getPlayerPositionById(playerId);
            if (current == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position next = move.nextPosition(current);
            Entity target = state.getEntity(next);
            if (target instanceof Wall) {
                return new Failed(action, "Wall blocks the way.");
            }
            if (target instanceof Player) {
                return new Failed(action, "Another player blocks the way.");
            }
            if (target instanceof Empty) {
                // plain move
                state.move(current, next);
                return new Success(action);
            }
            if (target instanceof Box) {
                Box box = (Box) target;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Box does not belong to this player.");
                }
                Position beyond = move.nextPosition(next);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity instanceof Wall) {
                    return new Failed(action, "Wall behind box.");
                }
                if (beyondEntity instanceof Player) {
                    return new Failed(action, "Player behind box.");
                }
                if (beyondEntity instanceof Box) {
                    return new Failed(action, "Box behind box.");
                }
                // push the box
                GameStateTransition transition = new GameStateTransition();
                transition.add(next, beyond);
                transition.add(current, next);
                state.move(current, next);
                state.move(next, beyond);
                state.recordTransition(transition, true);
                return new Success(action);
            }
            return new Failed(action, "Move failed.");
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

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}
