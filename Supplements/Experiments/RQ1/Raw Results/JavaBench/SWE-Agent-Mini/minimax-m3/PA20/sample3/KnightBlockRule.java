public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int absDx = Math.abs(dx - sx);
        int absDy = Math.abs(dy - sy);
        int blockX, blockY;
        if (absDx == 2 && absDy == 1) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else if (absDx == 1 && absDy == 2) {
            blockX = sx;
            blockY = (sy + dy) / 2;
        } else {
            return true;
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
