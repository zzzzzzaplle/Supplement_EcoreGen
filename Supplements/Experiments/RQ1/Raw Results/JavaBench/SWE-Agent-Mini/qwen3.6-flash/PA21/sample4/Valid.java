import java.util.Objects;

/**
 * A valid move result - the player moved successfully without dying.
 */
public class Valid extends MoveResult {

    private Position origPosition;

    public Valid() {
        super();
    }

    public Valid(Position newPosition, Position origPosition) {
        super(newPosition);
        this.origPosition = Objects.requireNonNull(origPosition);
    }

    /**
     * Gets the original position of the player before the move.
     *
     * @return the original position.
     */
    public Position getOrigPosition() {
        return origPosition;
    }

    /**
     * Sets the original position of the player before the move.
     *
     * @param origPosition the original position.
     */
    public void setOrigPosition(Position origPosition) {
        this.origPosition = Objects.requireNonNull(origPosition);
    }
}
