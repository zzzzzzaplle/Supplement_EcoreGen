public class FirstNMovesProtectionRule implements Rule {
    private int numProtectedMoves;
    public FirstNMovesProtectionRule() {}
    public void setNumProtectedMoves(int num) { this.numProtectedMoves = num; }
    public int getNumProtectedMoves() { return numProtectedMoves; }
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}