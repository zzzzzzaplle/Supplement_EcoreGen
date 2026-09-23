public class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place source = move.getSource();
        Place dest = move.getDestination();
        int sx = source.x();
        int sy = source.y();
        int dx = dest.x();
        int dy = dest.y();

        // Must be orthogonal
        if (sx != dx && sy != dy) {
            return false;
        }

        int size = game.getConfiguration().getSize();
        Piece destPiece = game.getPiece(dest);

        if (destPiece == null) {
            // Non-capturing move: path must be completely clear
            if (sx == dx) {
                // Horizontal movement (same x)
                int minY = Math.min(sy, dy);
                int maxY = Math.max(sy, dy);
                for (int y = minY + 1; y < maxY; y++) {
                    if (game.getPiece(sx, y) != null) {
                        return false;
                    }
                }
            } else {
                // Vertical movement (same y)
                int minX = Math.min(sx, dx);
                int maxX = Math.max(sx, dx);
                for (int x = minX + 1; x < maxX; x++) {
                    if (game.getPiece(x, sy) != null) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // Capturing move: exactly one piece between source and destination
            int betweenCount = 0;
            if (sx == dx) {
                int minY = Math.min(sy, dy);
                int maxY = Math.max(sy, dy);
                for (int y = minY + 1; y < maxY; y++) {
                    if (game.getPiece(sx, y) != null) {
                        betweenCount++;
                    }
                }
            } else {
                int minX = Math.min(sx, dx);
                int maxX = Math.max(sx, dx);
                for (int x = minX + 1; x < maxX; x++) {
                    if (game.getPiece(x, sy) != null) {
                        betweenCount++;
                    }
                }
            }
            return betweenCount == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
