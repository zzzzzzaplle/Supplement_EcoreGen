public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        if (!((adx == 2 && ady == 1) || (adx == 1 && ady == 2))) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
