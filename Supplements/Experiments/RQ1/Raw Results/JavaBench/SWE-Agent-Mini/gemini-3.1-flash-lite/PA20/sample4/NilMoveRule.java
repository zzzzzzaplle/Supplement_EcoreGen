public class NilMoveRule implements Rule {
    public NilMoveRule() {}
    public boolean validate(Game game, Move move) { return false; }
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
