public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public FirstNMovesProtectionRule() {}
    @Override
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}
