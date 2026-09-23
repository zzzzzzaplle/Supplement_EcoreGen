public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < numProtectedMoves) {
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());
            if (sourcePiece != null && destPiece != null) {
                return !sourcePiece.getPlayer().equals(destPiece.getPlayer());
            }
        }
        return true;
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
