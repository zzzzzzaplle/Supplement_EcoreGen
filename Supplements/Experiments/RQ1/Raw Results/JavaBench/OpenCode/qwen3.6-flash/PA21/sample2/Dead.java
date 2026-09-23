public class Dead extends Valid {
    public Position minePosition;

    public Dead() {
    }

    public Dead(Position minePosition) {
        this.minePosition = minePosition;
    }

    public Dead(Position origPosition, Position newPosition, Position minePosition) {
        super(origPosition, newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }
}