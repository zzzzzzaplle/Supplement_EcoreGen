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
        int dx = dst.x() - src.x();
        int dy = dst.y() - src.y();
        // Must move orthogonally
        if (dx != 0 && dy != 0) {
            return false;
        }
        if (dx == 0 && dy == 0) {
            return false;
        }
        int stepX = Integer.compare(dx, 0);
        int stepY = Integer.compare(dy, 0);
        int count = 0;
        int cx = src.x() + stepX;
        int cy = src.y() + stepY;
        while (cx != dst.x() || cy != dst.y()) {
            Piece p = game.getPiece(cx, cy);
            if (p != null) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
            cx += stepX;
            cy += stepY;
        }
        Piece destPiece = game.getPiece(dst);
        if (destPiece == null) {
            // Non-capturing: path must be clear.
            return count == 0;
        } else {
            // Capturing: exactly one piece between source and destination.
            return count == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
