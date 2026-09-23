public class NilMoveRule implements Rule {
    public NilMoveRule() {
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (move.getSource().equals(move.getDestination())) {
            return false;
        }
        return true;
    }
}
