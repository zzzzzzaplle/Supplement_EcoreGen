public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified;
    }

    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            this.isExitSpecified = true;
            return new Success(action);
        }
        if (action instanceof InvalidInput) {
            return new Failed(action, ((InvalidInput) action).getMessage());
        }
        if (action instanceof Undo) {
            if (this.state.getUndoQuota() == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (!this.state.hasCheckpoint()) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            this.state.undo();
            if (this.state.getUndoQuota() > 0) {
                this.state.consumeUndoQuota();
            }
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position from = this.state.getPlayerPositionById(initiator);
            if (from == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position to = move.nextPosition(from);
            Entity destEntity = this.state.getEntity(to);
            if (destEntity == null || destEntity instanceof Wall) {
                return new Failed(action, "Move blocked.");
            }
            if (destEntity instanceof Player) {
                return new Failed(action, "Move blocked.");
            }
            if (destEntity instanceof Empty) {
                GameStateTransition t = new GameStateTransition();
                t.add(from, to);
                this.state.recordTransition(t);
                this.state.move(from, to);
                return new Success(action);
            }
            if (destEntity instanceof Box) {
                Box box = (Box) destEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                Position beyond = move.nextPosition(to);
                Entity beyondEntity = this.state.getEntity(beyond);
                if (beyondEntity == null || beyondEntity instanceof Wall
                        || beyondEntity instanceof Player || beyondEntity instanceof Box) {
                    return new Failed(action, "Cannot push box.");
                }
                GameStateTransition t = new GameStateTransition();
                t.add(from, to);
                t.add(to, beyond);
                this.state.recordTransition(t);
                this.state.checkpoint();
                this.state.move(from, to);
                this.state.move(to, beyond);
                return new Success(action);
            }
            return new Failed(action, "Move blocked.");
        }
        return new Failed(action, "Unknown action");
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean isExitSpecified() {
        return this.isExitSpecified;
    }

    public void setExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }
}
