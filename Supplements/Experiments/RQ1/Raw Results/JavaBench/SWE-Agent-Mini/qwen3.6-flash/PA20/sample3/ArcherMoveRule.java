public class ArcherMoveRule implements Rule {
    
    public ArcherMoveRule() {
    }
    
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        
        Place source = move.getSource();
        Place destination = move.getDestination();
        
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        
        if (dx != 0 && dy != 0) {
            return false;
        }
        
        Piece destinationPiece = game.getPiece(destination);
        int screenCount = 0;
        
        if (dx != 0) {
            int step = dx > 0 ? 1 : -1;
            for (int x = source.x() + step; x != destination.x(); x += step) {
                if (game.getPiece(x, source.y()) != null) {
                    screenCount++;
                }
            }
        } else if (dy != 0) {
            int step = dy > 0 ? 1 : -1;
            for (int y = source.y() + step; y != destination.y(); y += step) {
                if (game.getPiece(source.x(), y) != null) {
                    screenCount++;
                }
            }
        }
        
        if (destinationPiece == null) {
            return screenCount == 0;
        } else {
            return screenCount == 1;
        }
    }
    
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
