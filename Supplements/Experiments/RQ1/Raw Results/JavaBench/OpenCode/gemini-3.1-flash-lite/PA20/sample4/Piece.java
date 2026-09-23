public abstract class Piece {
    private Player player;

    public Piece() {}

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public abstract char getLabel();
    public abstract Move[] getAvailableMoves(Game game, Place source);
}
