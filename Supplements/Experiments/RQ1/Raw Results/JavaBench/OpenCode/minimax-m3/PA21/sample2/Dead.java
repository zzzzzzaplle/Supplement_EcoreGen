public class Dead extends Valid {
    public Position minePosition;

    public Dead() {
        super();
    }

    public Dead(Position playerPosition, Position minePosition) {
        super(playerPosition, playerPosition);
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
