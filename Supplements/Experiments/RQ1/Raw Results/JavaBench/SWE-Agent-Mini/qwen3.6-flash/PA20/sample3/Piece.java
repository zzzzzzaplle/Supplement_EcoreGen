public abstract class Piece implements Cloneable {
    private Player player;
    
    public Piece() {
    }
    
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
    
    public abstract Move[] getAvailableMoves(Game game, Place source);
    
    @Override
    public Piece clone() throws CloneNotSupportedException {
        return (Piece) super.clone();
    }
}
