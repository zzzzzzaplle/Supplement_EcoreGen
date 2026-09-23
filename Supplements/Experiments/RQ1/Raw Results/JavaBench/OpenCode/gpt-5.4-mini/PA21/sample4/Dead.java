public class Dead extends Valid {
    private Position minePosition;

    public Dead() {
    }

    public Dead(Position newPosition, Position origPosition) {
        super(newPosition, origPosition);
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
