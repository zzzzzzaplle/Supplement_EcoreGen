public abstract class MoveResult {

    public Position newPosition;

    public MoveResult(Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}
