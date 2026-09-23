public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            return true;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        return !sourcePiece.getPlayer().equals(destPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
