public class Dead extends Valid {

    private Position minePosition;

    public Dead() {
    }

    public Dead(Position origPosition, Position newPosition, Position minePosition) {
        super(origPosition, newPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
