import java.util.Objects;

public abstract class Piece implements Cloneable {
    private Player player;

    public Piece() {
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);

    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return Objects.equals(player, piece.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
