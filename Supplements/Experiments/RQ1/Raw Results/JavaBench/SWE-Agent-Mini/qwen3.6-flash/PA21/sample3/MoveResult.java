import java.util.Objects;

/**
 * Abstract base class for move results.
 */
public abstract class MoveResult {
    protected Position newPosition;

    /**
     * Creates a move result with the specified new position.
     *
     * @param newPosition The new position after the move.
     */
    public MoveResult(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }

    /**
     * Returns the new position after the move.
     *
     * @return The new position.
     */
    public Position getNewPosition() {
        return newPosition;
    }

    /**
     * Sets the new position after the move.
     *
     * @param newPosition The new position.
     */
    public void setNewPosition(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }
}
