import java.util.Objects;

/**
 * A dead move result - the player hit a mine.
 */
public class Dead extends MoveResult {

    private Position minePosition;

    public Dead() {
        super();
        this.minePosition = null;
    }

    public Dead(Position newPosition, Position minePosition) {
        super(newPosition);
        this.minePosition = Objects.requireNonNull(minePosition);
    }

    /**
     * Gets the position of the mine that caused death.
     *
     * @return the mine position.
     */
    public Position getMinePosition() {
        return minePosition;
    }

    /**
     * Sets the position of the mine that caused death.
     *
     * @param minePosition the mine position.
     */
    public void setMinePosition(Position minePosition) {
        this.minePosition = Objects.requireNonNull(minePosition);
    }
}
