public class Box extends Entity {
    private int playerId;

    public Box() {
        super();
        this.playerId = 0;
    }

    public Box(int playerId) {
        super();
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
