public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {
    }

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    public int getNumProtectedMoves() {
        return numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // If we are still in the protection phase
        if (game.getNumMoves() < this.numProtectedMoves) {
            // Check if the destination has an opponent's piece (capture)
            Piece destPiece = game.getPiece(move.getDestination());
            if (destPiece != null) {
                Piece sourcePiece = game.getPiece(move.getSource());
                if (sourcePiece != null && !destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
