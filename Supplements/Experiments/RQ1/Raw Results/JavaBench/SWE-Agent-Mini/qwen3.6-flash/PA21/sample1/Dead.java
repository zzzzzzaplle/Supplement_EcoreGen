import java.util.Objects;

/**
 * Represents a move result where the player hit a mine and died.
 */
public class Dead extends MoveResult {
    public Position minePosition;

    /**
     * Creates a new Dead move result.
     *
     * @param newPosition   The position where the player stopped (original position).
     * @param minePosition  The position of the mine that caused death.
     */
    public Dead(Position newPosition, Position minePosition) {
        super(newPosition);
        this.minePosition = Objects.requireNonNull(minePosition);
    }

    /**
     * Gets the position of the mine.
     *
     * @return The mine position.
     */
    public Position getMinePosition() {
        return minePosition;
    }

    /**
     * Sets the position of the mine.
     *
     * @param minePosition The mine position.
     */
    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
