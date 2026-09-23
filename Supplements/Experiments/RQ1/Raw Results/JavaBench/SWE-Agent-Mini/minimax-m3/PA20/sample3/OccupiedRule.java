public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null) {
            return true;
        }
        if (destPiece != null && destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
