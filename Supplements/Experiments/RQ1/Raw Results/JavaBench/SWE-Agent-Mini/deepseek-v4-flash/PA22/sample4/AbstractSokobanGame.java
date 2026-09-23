public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame() {
        this.isExitSpecified = false;
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
            Failed failed = new Failed(action);
            failed.setReason(StringResources.INVALID_INPUT_MESSAGE);
            return failed;
        }
        
        if (action instanceof Undo) {
            Undo undo = (Undo) action;
            int playerId = undo.getInitiator();
            Position playerPos = state.getPlayerPositionById(playerId);
            if (playerPos == null) {
                Failed failed = new Failed(action);
                failed.setReason(StringResources.PLAYER_NOT_FOUND);
                return failed;
            }
            
            int quota = state.getUndoQuota();
            if (quota == 0) {
                Failed failed = new Failed(action);
                failed.setReason(StringResources.UNDO_QUOTA_RUN_OUT);
                return failed;
            }
            
            if (state.getHistory().isEmpty()) {
                Failed failed = new Failed(action);
                failed.setReason("Nothing to undo.");
                return failed;
            }
            
            state.undo();
            
            if (quota > 0) {
                state.setUndoQuota(quota - 1);
            }
            
            return new Success(action);
        }
        
        if (action instanceof Move) {
            Move move = (Move) action;
            int playerId = move.getInitiator();
            Position playerPos = state.getPlayerPositionById(playerId);
            
            if (playerPos == null) {
                Failed failed = new Failed(action);
                failed.setReason(StringResources.PLAYER_NOT_FOUND);
                return failed;
            }
            
            Position nextPos = move.nextPosition(playerPos);
            
            // Bounds check
            if (nextPos.x() < 0 || nextPos.x() >= state.getMapMaxWidth() ||
                nextPos.y() < 0 || nextPos.y() >= state.getMapMaxHeight()) {
                return new Failed(action);
            }
            
            Entity targetEntity = state.getEntity(nextPos);
            
            if (targetEntity == null || targetEntity instanceof Wall) {
                return new Failed(action);
            }
            
            if (targetEntity instanceof Player) {
                return new Failed(action);
            }
            
            if (targetEntity instanceof Empty) {
                // Move player to empty space
                state.move(playerPos, nextPos);
                return new Success(action);
            }
            
            if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != playerId) {
                    return new Failed(action);
                }
                
                Position behindBox = move.nextPosition(nextPos);
                
                // Check bounds for behind box
                if (behindBox.x() < 0 || behindBox.x() >= state.getMapMaxWidth() ||
                    behindBox.y() < 0 || behindBox.y() >= state.getMapMaxHeight()) {
                    return new Failed(action);
                }
                
                Entity behindEntity = state.getEntity(behindBox);
                
                if (behindEntity == null || behindEntity instanceof Wall || 
                    behindEntity instanceof Box || behindEntity instanceof Player) {
                    return new Failed(action);
                }
                
                // Push box and move player
                GameStateTransition transition = new GameStateTransition();
                transition.add(nextPos, behindBox);
                transition.add(playerPos, nextPos);
                
                state.move(nextPos, behindBox);
                state.move(playerPos, nextPos);
                
                // Record transition in history
                state.checkpoint();
                if (!state.getHistory().isEmpty()) {
                    int lastIdx = state.getHistory().size() - 1;
                    state.getHistory().set(lastIdx, transition);
                }
                
                return new Success(action);
            }
            
            return new Failed(action);
        }
        
        return new Failed(action);
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
