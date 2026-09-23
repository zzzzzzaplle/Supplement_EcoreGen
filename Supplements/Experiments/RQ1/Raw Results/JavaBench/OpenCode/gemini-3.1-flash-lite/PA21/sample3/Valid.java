import java.util.List;

public abstract class Valid extends MoveResult {
    private Position origPosition;

    public Valid() {}

    public Position getOrigPosition() { return origPosition; }
    public void setOrigPosition(Position origPosition) { this.origPosition = origPosition; }
}
