import java.util.Objects;

/**
 * Abstract base class for valid move results.
 */
public abstract class Valid extends MoveResult {
    protected Position origPosition;

    /**
     * Creates a valid move result with the specified original and new positions.
     *
     * @param origPosition The original position before the move.
     * @param newPosition  The new position after the move.
     */
    public Valid(Position origPosition, Position newPosition) {
        super(newPosition);
        this.origPosition = Objects.requireNonNull(origPosition);
    }

    /**
     * Returns the original position before the move.
     *
     * @return The original position.
     */
    public Position getOrigPosition() {
        return origPosition;
    }

    /**
     * Sets the original position before the move.
     *
     * @param origPosition The original position.
     */
    public void setOrigPosition(Position origPosition) {
        this.origPosition = Objects.requireNonNull(origPosition);
    }
}
