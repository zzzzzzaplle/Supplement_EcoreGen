public abstract class MoveResult {
    private Position newPosition;

    public MoveResult() {}

    public Position getNewPosition() { return newPosition; }
    public void setNewPosition(Position newPosition) { this.newPosition = newPosition; }
}
