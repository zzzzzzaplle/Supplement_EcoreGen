import java.util.Objects;

public abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;

    public Player() {
        this.name = "";
        this.score = 0;
        this.color = Color.DEFAULT;
    }

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.color = Color.DEFAULT;
    }

    public Player(String name, Color color) {
        this.name = name;
        this.score = 0;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract Move nextMove(Game game, Move[] availableMoves);

    @Override
    public Player clone() throws CloneNotSupportedException {
        return (Player) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
