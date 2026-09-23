public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        if (source.x() != dest.x() && source.y() != dest.y()) {
            return false;
        }
        
        int stepX = 0;
        int stepY = 0;
        
        if (source.x() != dest.x()) {
            stepX = (dest.x() > source.x()) ? 1 : -1;
        } else {
            stepY = (dest.y() > source.y()) ? 1 : -1;
        }
        
        int currentX = source.x() + stepX;
        int currentY = source.y() + stepY;
        
        int piecesBeforeDest = 0;
        
        while (currentX != dest.x() || currentY != dest.y()) {
            Place currentPlace = new Place(currentX, currentY);
            if (game.getPiece(currentPlace) != null) {
                piecesBeforeDest++;
            }
            currentX += stepX;
            currentY += stepY;
        }
        
        Piece destPiece = game.getPiece(dest);
        if (destPiece == null) {
            return piecesBeforeDest == 0;
        } else {
            return piecesBeforeDest == 1;
        }
    }
    
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
    
    public ArcherMoveRule() {
    }
}
