
/**
 * A box entity on the game board.
 */
public class Box extends Entity {
    private int playerId;

    public Box(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
