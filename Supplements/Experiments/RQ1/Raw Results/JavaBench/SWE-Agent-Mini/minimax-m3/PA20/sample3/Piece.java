public abstract class Piece {
    private Player player;

    public Piece() {
        this.player = null;
    }

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public abstract Move[] getAvailableMoves(Game game, Place source);
}
