/**
 * Represents a player in the Sokoban game.
 */
public class Player extends Entity {

    private int id;

    public Player() {
        this(0);
    }

    public Player(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
