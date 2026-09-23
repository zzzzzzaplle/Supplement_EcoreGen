public abstract class Valid extends MoveResult {

    private Position origPosition;

    public Valid() {
    }

    public Valid(Position origPosition, Position newPosition) {
        super(newPosition);
        this.origPosition = origPosition;
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = origPosition;
    }
}
