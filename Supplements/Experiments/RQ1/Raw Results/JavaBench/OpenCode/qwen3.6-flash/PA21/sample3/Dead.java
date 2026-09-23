public class Dead extends MoveResult {

    public Position minePosition;

    public Dead() {
    }

    public Dead(Position newPosition, Position minePosition) {
        this.newPosition = newPosition;
        this.minePosition = minePosition;
    }

    public Position getMinePosition() {
        return minePosition;
    }

    public void setMinePosition(Position minePosition) {
        this.minePosition = minePosition;
    }
}
