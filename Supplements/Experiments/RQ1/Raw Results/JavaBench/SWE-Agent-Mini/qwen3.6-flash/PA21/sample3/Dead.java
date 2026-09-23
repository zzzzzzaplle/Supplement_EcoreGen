import java.util.Objects;

/**
 * Move result indicating the player hit a mine and died.
 */
public class Dead extends MoveResult {
    private Position minePosition;

    public Dead() {
        super(null);
    }

    public Dead(Position newPosition) {
        super(newPosition);
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = Objects.requireNonNull(minePosition);
    }
}
