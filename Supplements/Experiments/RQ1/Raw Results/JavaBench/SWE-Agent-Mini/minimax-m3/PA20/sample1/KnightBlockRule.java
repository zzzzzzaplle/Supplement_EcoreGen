public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

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
        int tx = move.getDestination().x();
        int ty = move.getDestination().y();
        int absDx = Math.abs(tx - sx);
        int absDy = Math.abs(ty - sy);
        int bx, by;
        if (absDx == 2) {
            bx = (sx + tx) / 2;
            by = sy;
        } else if (absDy == 2) {
            bx = sx;
            by = (sy + ty) / 2;
        } else {
            return true;
        }
        int size = game.getConfiguration().getSize();
        if (bx < 0 || bx >= size || by < 0 || by >= size) {
            return true;
        }
        return game.getPiece(bx, by) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
