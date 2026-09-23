public class KnightMoveRule implements Rule {
    public KnightMoveRule() {}
    @Override
    public boolean validate(Game game, Move move) { return true; }
    public String getDescription() {
        return "knight move rule is violated";
    }
}
