
/**
 * A player entity on the game board.
 */
public class Player extends Entity {
    private int id;

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
