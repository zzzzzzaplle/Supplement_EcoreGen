public abstract class MoveResult {
    public Position newPosition;

    public MoveResult() {
        this.newPosition = null;
    }

    protected MoveResult(final Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}
