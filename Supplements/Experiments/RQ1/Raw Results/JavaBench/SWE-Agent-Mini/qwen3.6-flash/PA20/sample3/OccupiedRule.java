public class OccupiedRule implements Rule {
    
    public OccupiedRule() {
    }
    
    @Override
    public boolean validate(Game game, Move move) {
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece == null) {
            return true;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece != null && destinationPiece.getPlayer() == sourcePiece.getPlayer()) {
            return false;
        }
        return true;
    }
    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
