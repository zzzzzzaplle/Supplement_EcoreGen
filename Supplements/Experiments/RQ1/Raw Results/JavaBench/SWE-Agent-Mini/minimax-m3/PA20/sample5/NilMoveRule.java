public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (move.getSource().equals(move.getDestination())) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
