public class KnightMoveRule implements Rule {
    
    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        
        // Knight moves in L shape: (2,1) or (1,2)
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
            return false;
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
