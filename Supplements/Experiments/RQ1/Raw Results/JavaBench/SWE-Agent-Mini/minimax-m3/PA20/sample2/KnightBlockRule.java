public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        if (!((absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2))) {
            return true;
        }
        int blockX;
        int blockY;
        if (absDx == 2) {
            blockX = (move.getSource().x() + move.getDestination().x()) / 2;
            blockY = move.getSource().y();
        } else {
            blockX = move.getSource().x();
            blockY = (move.getSource().y() + move.getDestination().y()) / 2;
        }
        return game.getPiece(blockX, blockY) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
