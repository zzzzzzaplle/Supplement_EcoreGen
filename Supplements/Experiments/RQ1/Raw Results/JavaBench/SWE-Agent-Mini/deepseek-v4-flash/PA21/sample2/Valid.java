/**
 * Base class for valid move results.
 */
public class Valid extends MoveResult {

    private Position origPosition;

    public Valid() {
    }

    public Valid(final Position origPosition, final Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(final Position origPosition) {
        this.origPosition = origPosition;
    }
}
