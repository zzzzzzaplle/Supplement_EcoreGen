public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place destination = move.getDestination();
        if (source.x() < 0 || source.x() >= size || source.y() < 0 || source.y() >= size) {
            return false;
        }
        if (destination.x() < 0 || destination.x() >= size || destination.y() < 0 || destination.y() >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
