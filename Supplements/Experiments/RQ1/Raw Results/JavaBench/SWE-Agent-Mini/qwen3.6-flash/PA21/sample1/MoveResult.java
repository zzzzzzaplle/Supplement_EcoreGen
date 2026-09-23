/**
 * Base class for move results.
 */
public abstract class MoveResult {
    public Position newPosition;
    public MoveResult() {
    }
    /**
     * Creates a new MoveResult with the specified new position.
     *
     * @param newPosition The new position of the player.
     */
    public MoveResult(Position newPosition) {
        this.newPosition = newPosition;
    }

    /**
     * Gets the new position.
     *
     * @return The new position.
     */
    public Position getNewPosition() {
        return newPosition;
    }

    /**
     * Sets the new position.
     *
     * @param newPosition The new position.
     */
    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }
}
