public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece != null) {
            Piece sourcePiece = game.getPiece(move.getSource());
            if (sourcePiece != null && sourcePiece.getPlayer().equals(destPiece.getPlayer())) {
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
