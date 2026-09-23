public class Dead extends Valid {
    private final Position minePosition;

    public Dead(final Position origPosition, final Position newPosition, final Position minePosition) {
        super(origPosition, newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }
}
