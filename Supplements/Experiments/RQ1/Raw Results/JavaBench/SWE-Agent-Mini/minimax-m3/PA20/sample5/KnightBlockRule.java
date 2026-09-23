public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int blockX;
        int blockY;
        if (Math.abs(dx - sx) == 2) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else {
            blockX = sx;
            blockY = (sy + dy) / 2;
        }
        if (game.getPiece(blockX, blockY) != null) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
