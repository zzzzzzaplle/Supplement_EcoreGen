class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece == null) {
            return true;
        }
        return !destinationPiece.getPlayer().equals(game.getCurrentPlayer());
    }

@Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
