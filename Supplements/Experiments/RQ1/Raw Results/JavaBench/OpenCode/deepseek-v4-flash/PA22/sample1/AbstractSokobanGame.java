public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame() {
    }

    protected boolean shouldStop() {
        return isExitSpecified;
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
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            if (currentPos == null) {
                return new Failed();
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity nextEntity = state.getEntity(nextPos);
            if (nextEntity instanceof Wall) {
                return new Failed();
            }
            if (nextEntity instanceof Player) {
                return new Failed();
            }
            if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed();
                }
                Position beyondPos = move.nextPosition(nextPos);
                Entity beyondEntity = state.getEntity(beyondPos);
                if (!(beyondEntity instanceof Empty)) {
                    return new Failed();
                }
                GameStateTransition transition = new GameStateTransition();
                transition.add(nextPos, beyondPos);
                transition.add(currentPos, nextPos);
                state.move(nextPos, beyondPos);
                state.move(currentPos, nextPos);
                state.getHistory().push(transition);
                state.checkpoint();
                return new Success(action);
            }
            if (nextEntity instanceof Empty) {
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.getHistory().push(transition);
                return new Success(action);
            }
            return new Failed();
        }
        if (action instanceof InvalidInput) {
            return new Failed();
        }
        return new Failed();
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
