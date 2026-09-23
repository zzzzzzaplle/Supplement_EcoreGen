import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    private boolean isExitSpecified;

    public AbstractSokobanGame() {
        this.state = null;
        this.isExitSpecified = false;
    }

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
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

    protected boolean shouldStop() {
        return isExitSpecified || (state != null && state.isWin());
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
            if (state.getUndoQuota() == 0) {
                return new Failed(action, "Undo not allowed");
            }
            if (state.getHistory().isEmpty()) {
                return new Failed(action, "Nothing to undo");
            }
            if (state.getUndoQuota() > 0) {
                state.setUndoQuota(state.getUndoQuota() - 1);
            }
            undoAtomic();
            return new Success(action);
        }
        if (action instanceof Move) {
            Move move = (Move) action;
            int initiator = move.getInitiator();
            Position from = state.getPlayerPositionById(initiator);
            if (from == null) {
                return new Failed(action, "Player not found");
            }
            Position to = move.nextPosition(from);
            Entity dest = state.getEntity(to);
            if (dest instanceof Wall) {
                return new Failed(action, "Blocked by wall");
            }
            if (dest instanceof Player) {
                return new Failed(action, "Blocked by another player");
            }
            if (dest instanceof Empty) {
                GameStateTransition t = new GameStateTransition();
                t.add(from, to);
                state.pushHistory(t);
                state.move(from, to);
                return new Success(action);
            }
            if (dest instanceof Box) {
                Box box = (Box) dest;
                if (box.getPlayerId() != initiator) {
                    return new Failed(action, "Cannot push other player's box");
                }
                Position beyond = move.nextPosition(to);
                Entity beyondEntity = state.getEntity(beyond);
                if (beyondEntity instanceof Wall) {
                    return new Failed(action, "Box blocked by wall");
                }
                if (beyondEntity instanceof Player) {
                    return new Failed(action, "Box blocked by player");
                }
                if (beyondEntity instanceof Box) {
                    return new Failed(action, "Box blocked by box");
                }
                // Atomic transition for both player and box
                GameStateTransition t = new GameStateTransition();
                t.add(from, to);
                t.add(to, beyond);
                state.pushHistory(t);
                state.move(from, to);
                state.move(to, beyond);
                state.checkpoint();
                return new Success(action);
            }
            return new Failed(action, "Unknown entity");
        }
        return new Failed(action, "Unknown action");
    }

    private void undoAtomic() {
        // Pop transitions until we have undone a full checkpoint block
        // For simplicity, undo one transition at a time but only consume quota once
        // if a box move was involved.
        if (state.getHistory().isEmpty()) return;
        // Undo all transitions accumulated since last checkpoint
        // (We treat each push as part of a "block" that ends at a checkpoint call)
        // Simple approach: pop one transition per undo call.
        GameStateTransition t = state.getHistory().pop();
        GameStateTransition rev = t.reverse();
        for (java.util.Map.Entry<Position, Position> e : rev.getMoves().entrySet()) {
            Entity ent = state.getEntity(e.getValue());
            state.getBoard().put(e.getKey(), ent);
            state.getBoard().put(e.getValue(), new Empty());
            if (ent instanceof Player) {
                state.getPlayerPositions().put(((Player) ent).getId(), e.getKey());
            }
        }
    }
}
