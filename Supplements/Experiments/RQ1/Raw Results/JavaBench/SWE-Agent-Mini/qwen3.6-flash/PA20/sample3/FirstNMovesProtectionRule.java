public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    
    public FirstNMovesProtectionRule() {
        this.numProtectedMoves = 0;
    }
    
    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }
    
    @Override
    public boolean validate(Game game, Move move) {
        if (game.getNumMoves() < this.numProtectedMoves) {
            Piece destinationPiece = game.getPiece(move.getDestination());
            Piece sourcePiece = game.getPiece(move.getSource());
            if (destinationPiece != null && sourcePiece != null &&
                destinationPiece.getPlayer() != sourcePiece.getPlayer()) {
                return false;
            }
        }
        return true;
    }
    
     public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
