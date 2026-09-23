public class KnightMoveRule implements Rule {
    public KnightMoveRule() {
    }

    public boolean validate(Game game, Move move) {
        return true;
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
