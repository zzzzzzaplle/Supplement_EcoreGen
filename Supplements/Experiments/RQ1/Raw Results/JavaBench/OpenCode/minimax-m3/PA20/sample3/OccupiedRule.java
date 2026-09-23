public class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece source = game.getPiece(move.getSource());
        Piece destination = game.getPiece(move.getDestination());
        if (source == null || destination == null) {
            return true;
        }
        return !source.getPlayer().equals(destination.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
