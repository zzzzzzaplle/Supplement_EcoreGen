import java.util.ArrayList;
import java.util.Arrays;

public abstract class Piece implements Cloneable {
    protected Player player;

    public Piece() {}

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> candidates = computeMoves(game, source);
        return candidates.toArray(new Move[0]);
    }

    protected abstract ArrayList<Move> computeMoves(Game game, Place source);

    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }
}
