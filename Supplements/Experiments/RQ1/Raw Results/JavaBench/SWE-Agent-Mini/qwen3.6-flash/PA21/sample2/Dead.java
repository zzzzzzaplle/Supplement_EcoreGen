import java.util.Objects;

public class Dead extends MoveResult {
    private Position minePosition;

    public Dead() {
        super();
    }

    public Dead(Position newPosition, Position minePosition) {
        super(newPosition);
        this.minePosition = Objects.requireNonNull(minePosition);
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
