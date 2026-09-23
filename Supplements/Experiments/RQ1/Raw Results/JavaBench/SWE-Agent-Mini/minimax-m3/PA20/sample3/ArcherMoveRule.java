public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        // must be orthogonal
        if (sx != dx && sy != dy) {
            return false;
        }
        if (sx == dx && sy == dy) {
            return false;
        }
        int size = game.getConfiguration().getSize();
        Piece destPiece = game.getPiece(dx, dy);
        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        int between = 0;
        int cx = sx + stepX;
        int cy = sy + stepY;
        while (cx != dx || cy != dy) {
            if (game.getPiece(cx, cy) != null) {
                between++;
            }
            cx += stepX;
            cy += stepY;
        }
        if (destPiece == null) {
            // non-capturing move: path must be clear
            return between == 0;
        } else {
            // capturing move: exactly one piece between
            return between == 1;
        }
    }
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
