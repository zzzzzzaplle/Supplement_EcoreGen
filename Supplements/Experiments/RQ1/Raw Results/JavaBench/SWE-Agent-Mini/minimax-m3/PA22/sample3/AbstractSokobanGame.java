public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
        this.state = new GameState();
        this.isExitSpecified = false;
    }

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean getIsExitSpecified() {
        return this.isExitSpecified;
    }

    public void setIsExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }

    protected boolean shouldStop() {
        return this.isExitSpecified || this.state.isWin();
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
            this.state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = action.getInitiator();
            Position from = this.state.getPlayerPositionById(initiator);
            if (from == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position to = move.nextPosition(from);
            Entity target = this.state.getEntity(to);
            if (target instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            }
            if (target instanceof Player) {
                return new Failed(action, "Blocked by player");
            }
            if (target instanceof Box) {
                Box box = (Box) target;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push other player's box");
                }
                Position beyond = move.nextPosition(to);
                Entity beyondEntity = this.state.getEntity(beyond);
                if (beyondEntity instanceof Wall || beyondEntity instanceof Box || beyondEntity instanceof Player) {
                    return new Failed(action, "Cannot push box");
                }
                // Push box
                GameStateTransition transition = new GameStateTransition();
                transition.add(from, to);
                transition.add(to, beyond);
                this.state.addTransition(transition);
                this.state.move(from, to);
                this.state.move(to, beyond);
                this.state.checkpoint();
                return new Success(action);
            }
            // Empty
            GameStateTransition transition = new GameStateTransition();
            transition.add(from, to);
            this.state.addTransition(transition);
            this.state.move(from, to);
            return new Success(action);
        }
        return new Failed(action, "Unknown action");
    }
}
