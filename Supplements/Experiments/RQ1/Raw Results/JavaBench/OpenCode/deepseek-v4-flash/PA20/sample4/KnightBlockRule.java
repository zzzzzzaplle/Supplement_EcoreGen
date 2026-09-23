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
        Place block;
        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            block = new Place((move.getSource().x() + move.getDestination().x()) / 2, move.getSource().y());
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            block = new Place(move.getSource().x(), (move.getSource().y() + move.getDestination().y()) / 2);
        } else {
            return true;
        }
        return game.getPiece(block) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
