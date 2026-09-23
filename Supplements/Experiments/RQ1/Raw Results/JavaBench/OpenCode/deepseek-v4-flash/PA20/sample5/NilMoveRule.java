public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        return false;
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
