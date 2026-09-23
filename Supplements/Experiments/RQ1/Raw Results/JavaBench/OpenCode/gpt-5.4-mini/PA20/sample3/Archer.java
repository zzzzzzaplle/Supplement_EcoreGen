public class Archer extends Piece {
    public Archer() {
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        return new Move[0];
    }
}
