public class OccupiedRule implements Rule {
    public OccupiedRule() {
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }

    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();

        Piece sourcePiece = game.getPiece(source);
        Piece destPiece = game.getPiece(destination);

        if (destPiece == null) {
            return true;
        }

        Player sourcePlayer = sourcePiece.getPlayer();
        Player destPlayer = destPiece.getPlayer();

        if (sourcePlayer.equals(destPlayer)) {
            return false;
        }

        return true;
    }
}
