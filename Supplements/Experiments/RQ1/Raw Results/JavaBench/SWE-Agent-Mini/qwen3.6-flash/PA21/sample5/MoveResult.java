import java.util.Objects;

public abstract class MoveResult {

    protected Position newPosition;

    public MoveResult() {
    }

    public MoveResult(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = Objects.requireNonNull(newPosition);
    }
}
