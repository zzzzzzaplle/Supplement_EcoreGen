/**
 * Valid move result where the player hits a mine and dies.
 */
public class Dead extends Valid {

    private Position minePosition;

    public Dead() {
    }

    public Dead(final Position origPosition, final Position newPosition, final Position minePosition) {
        super(origPosition, newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(final Position minePosition) {
        this.minePosition = minePosition;
    }
}
