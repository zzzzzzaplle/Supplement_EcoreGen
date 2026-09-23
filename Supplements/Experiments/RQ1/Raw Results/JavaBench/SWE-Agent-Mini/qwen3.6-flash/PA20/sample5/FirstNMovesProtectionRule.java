public class FirstNMovesProtectionRule implements Rule {
    protected int numProtectedMoves;

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
        if (game.getNumMoves() < this.numProtectedMoves) {
            // Check if this is a capture move
            Piece destPiece = game.getPiece(move.getDestination());
            Piece srcPiece = game.getPiece(move.getSource());
            if (destPiece != null && srcPiece != null) {
                if (!destPiece.getPlayer().equals(srcPiece.getPlayer())) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
