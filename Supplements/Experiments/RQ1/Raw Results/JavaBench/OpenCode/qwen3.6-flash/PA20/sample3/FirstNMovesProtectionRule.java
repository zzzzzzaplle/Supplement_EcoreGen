class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;

    public FirstNMovesProtectionRule() {}

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game.getMovesPlayed() < this.numProtectedMoves) {
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());
            if (sourcePiece != null && destPiece != null) {
                return false;
            }
        }
        return true;
    }

 public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }

    public int getNumProtectedMoves() {
        return numProtectedMoves;
    }

    public void setNumProtectedMoves(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }
}
