import java.util.Deque;

public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    protected AbstractSokobanGame() {
    }

    public AbstractSokobanGame(GameState gameState) {
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
            int quota = state.getUndoQuota();
            if (quota == 0) {
                return new Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
            }
            
            Deque<GameStateTransition> history = state.getHistory();
            boolean hasCheckpoint = false;
            for (GameStateTransition t : history) {
                if (t == null) {
                    hasCheckpoint = true;
                    break;
                }
            }
            
            if (!hasCheckpoint) {
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
            Entity targetEntity = state.getEntity(nextPos);
            
            if (targetEntity == null) {
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.getHistory().push(transition);
                return new Success(action);
            } else if (targetEntity instanceof Empty) {
                GameStateTransition transition = new GameStateTransition();
                transition.add(currentPos, nextPos);
                state.move(currentPos, nextPos);
                state.getHistory().push(transition);
                return new Success(action);
            } else if (targetEntity instanceof Box) {
                Box box = (Box) targetEntity;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push another player's box.");
                }
                
                Position boxNextPos = move.nextPosition(nextPos);
                Entity behindBox = state.getEntity(boxNextPos);
                
                if (behindBox == null || behindBox instanceof Empty) {
                    GameStateTransition transition = new GameStateTransition();
                    transition.add(currentPos, nextPos);
                    transition.add(nextPos, boxNextPos);
                    
                    state.move(currentPos, nextPos);
                    state.move(nextPos, boxNextPos);
                    
                    state.getHistory().push(transition);
                    state.checkpoint();
                    
                    return new Success(action);
                } else {
                    return new Failed(action, "Cannot push box into a wall or entity.");
                }
            } else {
                return new Failed(action, "Cannot move into a wall or other player.");
            }
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

    public void setExitSpecified(boolean isExitSpecified) {
        this.isExitSpecified = isExitSpecified;
    }
}
