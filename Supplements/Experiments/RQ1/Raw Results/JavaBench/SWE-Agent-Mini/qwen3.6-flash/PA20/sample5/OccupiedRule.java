public class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Place dest = move.getDestination();
        Piece destPiece = game.getPiece(dest);
        Piece srcPiece = game.getPiece(move.getSource());
        
        if (destPiece != null && srcPiece != null) {
            if (destPiece.getPlayer().equals(srcPiece.getPlayer())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
