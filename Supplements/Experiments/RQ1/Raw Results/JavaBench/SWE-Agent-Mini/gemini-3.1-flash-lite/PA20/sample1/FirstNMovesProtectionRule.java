public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public FirstNMovesProtectionRule() {}
    public void setNumProtectedMoves(int numProtectedMoves) { this.numProtectedMoves = numProtectedMoves; }
    @Override
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
