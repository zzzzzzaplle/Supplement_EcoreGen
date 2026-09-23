public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
