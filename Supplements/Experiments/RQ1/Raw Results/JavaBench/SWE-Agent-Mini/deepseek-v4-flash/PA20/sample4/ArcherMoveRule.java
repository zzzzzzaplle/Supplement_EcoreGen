public class ArcherMoveRule implements Rule {

    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        // TODO implementation
        Place src = move.getSource();
        Place dst = move.getDestination();
        int srcX = src.getX();
        int srcY = src.getY();
        int dstX = dst.getX();
        int dstY = dst.getY();

        // Must be orthogonal
        if (srcX != dstX && srcY != dstY) {
            return false;
        }

        int dx = Integer.signum(dstX - srcX);
        int dy = Integer.signum(dstY - srcY);
        int piecesBetween = 0;

        int curX = srcX + dx;
        int curY = srcY + dy;
        while (curX != dstX || curY != dstY) {
            if (game.getPiece(curX, curY) != null) {
                piecesBetween++;
            }
            curX += dx;
            curY += dy;
        }

        Piece destPiece = game.getPiece(dst);
        if (destPiece == null) {
            // Non-capturing move: path must be completely clear
            return piecesBetween == 0;
        } else {
            // Capturing move: exactly one piece between source and destination
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
