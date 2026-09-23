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
        if (game == null || move == null) {
            return true;
        }
        if (game.getNumMoves() >= this.numProtectedMoves) {
            return true;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        Piece destPiece = game.getPiece(move.getDestination());
        if (sourcePiece == null || destPiece == null) {
            return true;
        }
        return sourcePiece.getPlayer().equals(destPiece.getPlayer());
    }

 public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
