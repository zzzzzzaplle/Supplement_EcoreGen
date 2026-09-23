public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
        this.numProtectedMoves = 0;
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    public int getNumProtectedMoves() {
        return this.numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() >= this.numProtectedMoves) {
            return true;
        }
        // during protection, no capture allowed
        Piece destPiece = game.getPiece(move.getDestination());
        return destPiece == null;
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
