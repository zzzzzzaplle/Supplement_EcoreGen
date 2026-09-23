public class KnightMoveRule implements Rule {

    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
