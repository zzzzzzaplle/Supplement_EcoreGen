public class Valid extends MoveResult {
    private Position origPosition;

    public Valid() {}
    public Position getOrigPosition() { return origPosition; }
    public void setOrigPosition(Position pos) { this.origPosition = pos; }
}
