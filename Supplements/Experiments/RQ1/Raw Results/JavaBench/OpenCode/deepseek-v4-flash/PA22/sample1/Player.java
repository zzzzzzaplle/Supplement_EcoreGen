public class Player extends Entity {
    private int id;

    public Player(int id) {
        this.id = id;
    }

    public Player() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
