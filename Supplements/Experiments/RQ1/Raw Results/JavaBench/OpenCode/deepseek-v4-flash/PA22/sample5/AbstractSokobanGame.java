public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame() {
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
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(playerId);
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity entityAtNext = state.getEntity(nextPos);
            if (entityAtNext instanceof Wall) {
                return new Failed(action, "Cannot move into wall");
            }
            if (entityAtNext instanceof Player) {
                return new Failed(action, "Cannot move into another player");
            }
            if (entityAtNext instanceof Box) {
                Box box = (Box) entityAtNext;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action, "Cannot push another player's box");
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity entityBehind = state.getEntity(behindPos);
                if (!(entityBehind instanceof Empty) && entityBehind != null) {
                    return new Failed(action, "Cannot push box into obstacle");
                }
                state.move(currentPos, nextPos);
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                transition.add(nextPos, behindPos);
                GameStateTransition finalTransition = transition;
                state.checkpoint();
                state.move(nextPos, behindPos);
                return new Success(action);
            }
            if (entityAtNext instanceof Empty || entityAtNext == null) {
                state.move(currentPos, nextPos);
                return new Success(action);
            }
            return new Failed(action, "Cannot move");
        }
        return new Failed(action, "Unknown action");
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}
