public class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();
        
        // Archer moves orthogonally
        if (srcX != destX && srcY != destY) {
            return false;
        }
        
        // Check the cells between source and destination
        int dx = Integer.compare(destX - srcX, 0);
        int dy = Integer.compare(destY - srcY, 0);
        
        boolean foundScreen = false;
        if (dx != 0) {
            for (int x = srcX + dx; x != destX; x += dx) {
                Piece piece = game.getPiece(x, srcY);
                if (piece != null) {
                    if (foundScreen) {
                        return false;
                    }
                    foundScreen = true;
                }
            }
        } else if (dy != 0) {
            for (int y = srcY + dy; y != destY; y += dy) {
                Piece piece = game.getPiece(srcX, y);
                if (piece != null) {
                    if (foundScreen) {
                        return false;
                    }
                    foundScreen = true;
                }
            }
        }
        
        return true;
    }

    public String getDescription() {
        return "archer move rule is violated";
    }
}
