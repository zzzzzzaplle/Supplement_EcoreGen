public class OccupiedRule implements Rule {

    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Place destination = move.getDestination();
        Piece destPiece = game.getPiece(destination);
        if (destPiece == null) {
            return true; // destination is empty, so it's fine
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            return true;
        }
        // The destination cannot contain a friendly piece
        return !sourcePiece.getPlayer().equals(destPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
