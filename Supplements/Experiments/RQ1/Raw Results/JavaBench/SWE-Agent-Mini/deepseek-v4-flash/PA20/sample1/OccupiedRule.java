public class OccupiedRule implements Rule {
    
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece != null && sourcePiece != null && 
            destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
