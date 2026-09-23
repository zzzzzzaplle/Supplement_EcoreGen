public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        
        int dx = Math.abs(destX - srcX);
        int dy = Math.abs(destY - srcY);
        
        int midX, midY;
        if (dx == 2) {
            midX = srcX + (destX - srcX) / 2;
            midY = srcY;
        } else if (dy == 2) {
            midX = srcX;
            midY = srcY + (destY - srcY) / 2;
        } else {
            return true;
        }

        Piece blockingPiece = game.getPiece(midX, midY);
        if (blockingPiece != null) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
