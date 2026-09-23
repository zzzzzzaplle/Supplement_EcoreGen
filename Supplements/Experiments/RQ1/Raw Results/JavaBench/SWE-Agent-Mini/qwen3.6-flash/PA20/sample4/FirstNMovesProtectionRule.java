public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < numProtectedMoves) {
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());
            if (sourcePiece != null && destPiece != null) {
                return false; // capture not allowed during protection
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
