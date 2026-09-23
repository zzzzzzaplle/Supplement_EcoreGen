public class KnightMoveRule implements Rule {

    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().getX() - move.getSource().getX());
        int dy = Math.abs(move.getDestination().getY() - move.getSource().getY());
        // Knight moves in L-shape: (2,1) or (1,2)
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
