public class Box extends Entity {
    private int playerId;

    public Box() {}
    public Box(int playerId) { this.playerId = playerId; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}
