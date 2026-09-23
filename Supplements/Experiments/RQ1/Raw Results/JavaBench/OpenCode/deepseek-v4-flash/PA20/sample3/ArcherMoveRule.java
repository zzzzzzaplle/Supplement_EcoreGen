public class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place src = move.getSource();
        Place dst = move.getDestination();
        int sx = src.x(), sy = src.y();
        int dx = dst.x(), dy = dst.y();

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

        Piece destPiece = game.getPiece(dst);
        if (destPiece == null) {
            return piecesBetween == 0;
        } else {
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
