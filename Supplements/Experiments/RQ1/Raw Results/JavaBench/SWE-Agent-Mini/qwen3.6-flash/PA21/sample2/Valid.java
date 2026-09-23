import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Valid extends MoveResult {
    public Position origPosition;

    public Valid() {
        super();
    }

    public Valid(Position newPosition, Position origPosition) {
        super(newPosition);
        this.origPosition = Objects.requireNonNull(origPosition);
    }

    public Position getOrigPosition() {
        return origPosition;
    }

    public void setOrigPosition(Position origPosition) {
        this.origPosition = Objects.requireNonNull(origPosition);
    }
}
