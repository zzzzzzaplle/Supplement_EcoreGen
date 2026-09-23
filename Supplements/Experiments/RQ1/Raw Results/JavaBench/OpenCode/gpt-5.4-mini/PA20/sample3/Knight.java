public class Knight extends Piece {
    public Knight() {
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        return new Move[0];
    }
}
