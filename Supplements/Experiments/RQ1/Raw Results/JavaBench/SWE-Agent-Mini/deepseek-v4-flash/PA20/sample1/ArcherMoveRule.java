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
        int sx = source.x(), sy = source.y();
        int dx = dest.x(), dy = dest.y();
        
        // Must be orthogonal
        if (sx != dx && sy != dy) {
            return false;
        }
        
        int piecesBetween = 0;
        if (sx == dx) {
            int step = (dy > sy) ? 1 : -1;
            for (int y = sy + step; y != dy; y += step) {
                if (game.getPiece(sx, y) != null) {
                    piecesBetween++;
                }
            }
        } else {
            int step = (dx > sx) ? 1 : -1;
            for (int x = sx + step; x != dx; x += step) {
                if (game.getPiece(x, sy) != null) {
                    piecesBetween++;
                }
            }
        }
        
        Piece destPiece = game.getPiece(dest);
        if (destPiece == null && piecesBetween == 0) {
            return true;
        }
        if (destPiece != null && piecesBetween == 1 && !destPiece.getPlayer().equals(game.getPiece(source).getPlayer())) {
            return true;
        }
        
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
