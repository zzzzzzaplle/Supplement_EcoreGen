public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int bx, by;
        if (Math.abs(dx) == 2) {
            bx = (move.getSource().x() + move.getDestination().x()) / 2;
            by = move.getSource().y();
        } else {
            bx = move.getSource().x();
            by = (move.getSource().y() + move.getDestination().y()) / 2;
        }
        return game.getPiece(bx, by) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
