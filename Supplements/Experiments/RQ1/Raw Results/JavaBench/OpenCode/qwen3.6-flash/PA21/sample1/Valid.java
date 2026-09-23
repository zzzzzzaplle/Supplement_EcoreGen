public class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
    }

    protected Valid(final Position origPosition, final Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }
}
