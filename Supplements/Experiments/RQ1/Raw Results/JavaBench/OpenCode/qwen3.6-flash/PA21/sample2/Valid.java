public abstract class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
    }

    public Valid(Position origPosition, Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }
}