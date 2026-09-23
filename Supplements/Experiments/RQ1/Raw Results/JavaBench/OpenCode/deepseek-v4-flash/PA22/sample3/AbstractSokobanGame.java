public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
    }

    public AbstractSokobanGame(GameState gameState) {
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
        if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            Position nextPos = move.nextPosition(currentPos);
            Entity nextEntity = state.getEntity(nextPos);
            if (nextEntity instanceof Empty) {
                state.move(currentPos, nextPos);
                return new Success(action);
            }
            if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed();
                }
                Position behindPos = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindPos);
                if (behindEntity instanceof Empty) {
                    state.move(currentPos, nextPos);
                    return new Success(action);
                }
                return new Failed();
            }
            return new Failed();
        }
        return new Failed();
    }
}
