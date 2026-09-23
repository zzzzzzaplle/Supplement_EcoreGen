public class ArcherMoveRule implements Rule {

    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place src = move.getSource();
        Place dest = move.getDestination();
        int srcX = src.getX();
        int srcY = src.getY();
        int destX = dest.getX();
        int destY = dest.getY();

        // Must be orthogonal (same row or same column)
        if (srcX != destX && srcY != destY) {
            return false;
        }

        Piece destPiece = game.getPiece(dest);
        int dx = Integer.signum(destX - srcX);
        int dy = Integer.signum(destY - srcY);
        int steps = Math.max(Math.abs(destX - srcX), Math.abs(destY - srcY));
        int screenCount = 0;

        // Count pieces between source and destination (exclusive)
        for (int i = 1; i < steps; i++) {
            int checkX = srcX + dx * i;
            int checkY = srcY + dy * i;
            Place checkPlace = new Place(checkX, checkY);
            if (game.getPiece(checkPlace) != null) {
                screenCount++;
            }
        }

        if (destPiece == null) {
            // Non-capturing move: path must be completely clear
            return screenCount == 0;
        } else {
            // Capturing move: there must be exactly one screen piece
            return screenCount == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
