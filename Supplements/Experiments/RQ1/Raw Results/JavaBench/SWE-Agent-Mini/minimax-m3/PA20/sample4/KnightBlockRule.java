public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
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
        if (!((absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2))) {
            return true;
        }
        int blockX, blockY;
        if (absDx == 2) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else {
            blockX = sx;
            blockY = (sy + dy) / 2;
        }
        return game.getPiece(blockX, blockY) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
