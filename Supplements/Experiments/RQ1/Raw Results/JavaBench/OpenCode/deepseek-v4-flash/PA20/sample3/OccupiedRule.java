public class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece destPiece = game.getPiece(move.getDestination());
        Piece srcPiece = game.getPiece(move.getSource());
        if (destPiece == null) {
            return true;
        }
        if (srcPiece == null) {
            return false;
        }
        return !srcPiece.getPlayer().equals(destPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
