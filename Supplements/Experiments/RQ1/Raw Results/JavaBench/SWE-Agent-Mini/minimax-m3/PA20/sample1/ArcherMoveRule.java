public class ArcherMoveRule implements Rule {

    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int tx = move.getDestination().x();
        int ty = move.getDestination().y();
        // must be orthogonal
        if (sx != tx && sy != ty) {
            return false;
        }
        int size = game.getConfiguration().getSize();
        int stepX = Integer.compare(tx, sx);
        int stepY = Integer.compare(ty, sy);
        int piecesBetween = 0;
        int cx = sx + stepX;
        int cy = sy + stepY;
        while (cx != tx || cy != ty) {
            if (cx < 0 || cx >= size || cy < 0 || cy >= size) {
                return false;
            }
            if (game.getPiece(cx, cy) != null) {
                piecesBetween++;
            }
            cx += stepX;
            cy += stepY;
        }
        Piece dest = game.getPiece(move.getDestination());
        if (dest == null) {
            // non-capturing move: path must be clear
            return piecesBetween == 0;
        } else {
            // capturing move: exactly one piece between as screen
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
