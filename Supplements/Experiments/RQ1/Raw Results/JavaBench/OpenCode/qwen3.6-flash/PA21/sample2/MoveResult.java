public abstract class MoveResult {
    protected Position newPosition;

    public MoveResult() {
    }

    public MoveResult(Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}