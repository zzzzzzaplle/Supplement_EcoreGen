public class Player extends Entity {
    private int id;

    public Player() {
        super();
        this.id = 0;
    }

    public Player(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
