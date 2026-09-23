
import java.util.*;

/**
 * Abstract implementation of SokobanGame.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    protected GameState state;
    protected boolean isExitSpecified;

    public AbstractSokobanGame(GameState gameState) {
        this.state = gameState;
        this.isExitSpecified = false;
    }

    /**
     * Check if the game should stop.
     * @return true if the game should stop
     */
    protected boolean shouldStop() {
        return this.isExitSpecified || state.isWin();
    }

    /**
     * Process an action.
     * @param action the action to process
     * @return the result of the action
     */
    protected ActionResult processAction(Action action) {
        if (action instanceof Exit) {
            isExitSpecified = true;
            return new Success(action);
        }

        if (action instanceof Undo) {
            return processUndo(action);
        }

        if (action instanceof InvalidInput) {
            return new Failed(StringResources.INVALID_INPUT_MESSAGE);
        }

        if (action instanceof Move) {
            return processMove((Move) action);
        }

        return new Failed("Unknown action");
    }

    private ActionResult processMove(Move move) {
        int initiator = move.getInitiator();
        Position playerPos = state.getPlayerPositionById(initiator);
        if (playerPos == null) {
            return new Failed(StringResources.PLAYER_NOT_FOUND);
        }

        Position nextPos = move.nextPosition(playerPos);

        // Check if next position is valid
        if (!isValidPosition(nextPos)) {
            return new Failed("Move out of bounds");
        }

        Entity entity = state.getEntity(nextPos);

        if (entity instanceof Wall) {
            return new Failed("Cannot move into a wall");
        }

        if (entity instanceof Player) {
            return new Failed("Cannot move into another player");
        }

        if (entity instanceof Box) {
            Box box = (Box) entity;
            // Check if box belongs to this player
            if (box.getPlayerId() != initiator) {
                return new Failed("Cannot push another player's box");
            }
            // Check position behind the box
            Position boxBehindPos = move.nextPosition(nextPos);
            if (!isValidPosition(boxBehindPos)) {
                return new Failed("Cannot push box out of bounds");
            }
            Entity boxBehind = state.getEntity(boxBehindPos);
            if (boxBehind instanceof Wall) {
                return new Failed("Cannot push box into a wall");
            }
            if (boxBehind instanceof Player) {
                return new Failed("Cannot push box into another player");
            }
            if (boxBehind instanceof Box) {
                return new Failed("Cannot push box into another box");
            }

            // Determine if this push is on a destination (checkpoint)
            boolean isCheckpoint = state.getDestinations().contains(nextPos);

            // Perform moves: first move the box, then move the player
            Position afterBoxPos = move.nextPosition(nextPos);
            state.move(nextPos, afterBoxPos); // Move box
            state.recordMove(nextPos, afterBoxPos); // Record box move

            state.move(playerPos, nextPos);   // Move player into box's old position
            state.recordMove(playerPos, nextPos); // Record player move

            if (isCheckpoint) {
                state.checkpoint();
            }

            return new Success(move);
        }

        // Empty space - just move
        state.move(playerPos, nextPos);
        state.recordMove(playerPos, nextPos);
        return new Success(move);
    }

    private boolean isValidPosition(Position pos) {
        return pos.x() >= 0 && pos.x() < state.getMapMaxWidth()
                && pos.y() >= 0 && pos.y() < state.getMapMaxHeight();
    }

    private ActionResult processUndo(Action action) {
        Optional<Integer> undoLimitOpt = state.getUndoLimit();
        if (undoLimitOpt.isPresent()) {
            int undoLimit = undoLimitOpt.get();
            if (undoLimit == 0) {
                // Undo not allowed
                return new Failed(StringResources.UNDO_QUOTA_RUN_OUT);
            }
            if (undoLimit > 0) {
                if (state.getUndoQuota() <= 0) {
                    return new Failed(StringResources.UNDO_QUOTA_RUN_OUT);
                }
                state.setUndoQuota(state.getUndoQuota() - 1);
            }
            // For unlimited (-1), no quota check needed and no consumption
        }

        state.undo();
        return new Success(action);
    }
}
