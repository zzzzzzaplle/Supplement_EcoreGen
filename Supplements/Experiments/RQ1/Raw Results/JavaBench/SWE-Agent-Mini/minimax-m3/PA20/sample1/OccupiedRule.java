public class OccupiedRule implements Rule {

    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece source = game.getPiece(move.getSource());
        Piece dest = game.getPiece(move.getDestination());
        if (source == null) {
            return true;
        }
        if (dest == null) {
            return true;
        }
        // destination occupied by friendly piece -> invalid
        return !source.getPlayer().equals(dest.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
