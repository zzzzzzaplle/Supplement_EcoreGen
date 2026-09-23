/**
 * Abstract base class for the result of a move.
 */
public abstract class MoveResult {

    private Position newPosition;

    public MoveResult() {
    }

    public MoveResult(final Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(final Position newPosition) {
        this.newPosition = newPosition;
    }
}
