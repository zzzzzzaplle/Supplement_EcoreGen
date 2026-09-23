public abstract class Piece {
    private Player player;

    public Piece() {}

    public abstract char getLabel();
    public abstract Move[] getAvailableMoves(Game game, Place source);

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}