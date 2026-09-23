public class NilMoveRule implements Rule {
    public NilMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source == null || destination == null) {
            return false;
        }
        return !source.equals(destination);
    }

 public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
