import java.util.ArrayList;
import java.util.List;

public class Valid extends MoveResult {
    private Position origPosition;

    public Valid() {
    }

    public Valid(Position newPosition, Position origPosition) {
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
