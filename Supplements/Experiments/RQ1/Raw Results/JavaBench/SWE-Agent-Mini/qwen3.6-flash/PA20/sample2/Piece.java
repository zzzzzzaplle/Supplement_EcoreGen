import java.util.*;

public abstract class Piece implements Cloneable {
    private Player player;

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);
    
    public Piece() {
    }
}
