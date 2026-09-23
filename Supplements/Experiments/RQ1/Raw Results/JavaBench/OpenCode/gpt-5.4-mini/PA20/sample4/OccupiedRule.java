public class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destinationPiece = game.getPiece(move.getDestination());
        return destinationPiece == null || sourcePiece == null || !destinationPiece.getPlayer().equals(sourcePiece.getPlayer());
    }

@Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
