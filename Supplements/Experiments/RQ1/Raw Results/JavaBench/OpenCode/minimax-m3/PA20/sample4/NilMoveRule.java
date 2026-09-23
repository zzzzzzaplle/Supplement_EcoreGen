public class NilMoveRule implements Rule {

    public NilMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        return !move.getSource().equals(move.getDestination());
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
