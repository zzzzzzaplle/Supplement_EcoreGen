import java.util.Objects;

public abstract class Player implements Cloneable {
    protected String name;
    protected int score;
    protected Color color;
    public Player() {}
    public abstract Move nextMove(Game game, Move[] availableMoves);
    public Player clone() throws CloneNotSupportedException {
        return (Player) super.clone();
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }
    public int hashCode() {
        return Objects.hash(name);
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}
