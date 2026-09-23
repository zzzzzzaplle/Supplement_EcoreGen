/**
 * Represents a valid move result where the player did not die.
 */
public class Valid extends MoveResult {
    public Position origPosition;

    /**
     * Creates a new Valid move result with the specified original and new positions.
     *
     * @param origPosition The original position of the player.
     * @param newPosition  The new position of the player.
     */
    public Valid(Position origPosition, Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }
    public Valid() {
    }
    /**
     * Gets the original position.
     *
     * @return The original position.
     */
    public Position getOrigPosition() {
        return origPosition;
    }

    /**
     * Sets the original position.
     *
     * @param origPosition The original position.
     */
    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}
