public class Dead extends Valid {
    public Position minePosition;

    public Dead() {
        super();
        this.minePosition = null;
    }

    public Dead(Position newPosition, Position origPosition) {
        super(newPosition, origPosition);
        this.minePosition = null;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
