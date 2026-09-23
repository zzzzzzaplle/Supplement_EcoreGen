import java.util.Objects;

/**
 * Abstract base class for move results.
 */
public abstract class MoveResult {

    private Position newPosition;

    public MoveResult() {
    }

    public MoveResult(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }

    /**
     * Gets the new position after the move.
     *
     * @return the new position.
     */
    public Position getNewPosition() {
        return newPosition;
    }

    /**
     * Sets the new position after the move.
     *
     * @param newPosition the new position.
     */
    public void setNewPosition(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }
}
