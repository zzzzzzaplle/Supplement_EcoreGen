public class Knight extends Piece {
    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        return new Move[0];
    }
}
