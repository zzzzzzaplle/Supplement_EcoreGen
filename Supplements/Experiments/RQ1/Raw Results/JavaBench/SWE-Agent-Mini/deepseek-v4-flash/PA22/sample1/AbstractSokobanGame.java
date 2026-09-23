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
            return new Failed(action, ((InvalidInput) action).getMessage());
        } else if (action instanceof Undo) {
            // Check undo quota
            if (state.getUndoQuota() == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            // Check if there are checkpoints to undo
            if (state.getCheckpointHistory().isEmpty()) {
                return new Failed(action, "Nothing to undo.");
            }
            state.undo();
            return new Success(action);
        } else if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position currentPos = state.getPlayerPositionById(initiator);
            
            if (currentPos == null) {
                return new Failed(action, StringResources.PLAYER_NOT_FOUND);
            }
            
            Position nextPos = move.nextPosition(currentPos);
            Entity nextEntity = state.getEntity(nextPos);
            
            if (nextEntity == null || nextEntity instanceof Empty || nextEntity instanceof Wall) {
                // null or empty - player just moves into empty space
                if (nextEntity instanceof Wall) {
                    return new Failed(action, "Cannot move into wall.");
                }
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.getMoveHistory().push(transition);
                return new Success(action);
            } else if (nextEntity instanceof Box) {
                Box box = (Box) nextEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                // Check if the space behind the box is empty
                Position behindPos = move.nextPosition(nextPos);
                Entity behindEntity = state.getEntity(behindPos);
                if (behindEntity == null || behindEntity instanceof Empty) {
                    // Push the box
                    GameStateTransition transition = new GameStateTransition();
                    transition.add(currentPos, nextPos);
                    transition.add(nextPos, behindPos);
                    state.move(currentPos, nextPos);
                    state.move(nextPos, behindPos);
                    state.getMoveHistory().push(transition);
                    // Check if box is on a destination -> checkpoint
                    if (state.getDestinations().contains(behindPos)) {
                        state.checkpoint();
                    }
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box into blocked space.");
                }
            } else if (nextEntity instanceof Player) {
                return new Failed(action, "Cannot move into another player.");
            }
            
            return new Failed(action, "Invalid move.");
        }
        
        return new Failed(action, "Unknown action.");
    }

    public boolean isExitSpecified() {
        return isExitSpecified;
    }

    public void setExitSpecified(boolean exitSpecified) {
        isExitSpecified = exitSpecified;
    }
}
