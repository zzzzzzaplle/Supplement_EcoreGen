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
        } else if (action instanceof InvalidInput) {
            return new Failed();
        } else if (action instanceof Undo) {
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            if (currentPos == null) {
                Failed failed = new Failed();
                failed.setReason(StringResources.PLAYER_NOT_FOUND);
                return failed;
            }
            Position nextPos = move.nextPosition(currentPos);
            Entity targetEntity = state.getEntity(nextPos);
            
            if (targetEntity == null || targetEntity instanceof Wall) {
                Failed failed = new Failed();
                failed.setReason("Blocked by wall");
                return failed;
            }
            
            if (targetEntity instanceof Player) {
                Failed failed = new Failed();
                failed.setReason("Blocked by another player");
                return failed;
            }
            
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != initiator) {
                    Failed failed = new Failed();
                    failed.setReason("Cannot push another player's box");
                    return failed;
                }
                Position behindBox = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindBox);
                if (behindEntity != null && !(behindEntity instanceof Empty)) {
                    Failed failed = new Failed();
                    failed.setReason("Box cannot be pushed");
                    return failed;
                }
                // Push box
                GameStateTransition transition = new GameStateTransition();
                transition.add(nextPos, behindBox);
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.move(nextPos, behindBox);
                state.getHistory().push(transition);
                return new Success(action);
            }
            
            // Empty space - just move player
            GameStateTransition transition = new GameStateTransition();
            transition.add(currentPos, nextPos);
            state.move(currentPos, nextPos);
            return new Success(action);
        }
        
        Failed failed = new Failed();
        failed.setReason("Unknown action");
        return failed;
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}
