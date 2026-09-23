public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        int blockX, blockY;
        if (Math.abs(dx) == 2) {
            blockX = move.getSource().x() + dx / 2;
            blockY = move.getSource().y();
        } else {
            blockX = move.getSource().x();
            blockY = move.getSource().y() + dy / 2;
        }
        Piece blockingPiece = game.getPiece(blockX, blockY);
        if (blockingPiece != null) {
            return false;
        }
        return true;
    }

public String getDescription() {
        return "knight is blocked by another piece";
    }
}
