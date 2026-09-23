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
        if (sx != dx && sy != dy) {
            return false;
        }
        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        int piecesBetween = 0;
        int cx = sx + stepX;
        int cy = sy + stepY;
        while (cx != dx || cy != dy) {
            if (game.getPiece(cx, cy) != null) {
                piecesBetween++;
            }
            cx += stepX;
            cy += stepY;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            // non-capturing move: path must be clear
            return piecesBetween == 0;
        } else {
            // capturing move: exactly one screen piece between
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
