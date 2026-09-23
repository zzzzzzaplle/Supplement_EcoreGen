public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return false;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
