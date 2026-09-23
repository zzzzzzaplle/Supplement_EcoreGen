public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        int size = game.getConfiguration().getSize();
        Place s = move.getSource();
        Place d = move.getDestination();
        if (s.x() < 0 || s.x() >= size || s.y() < 0 || s.y() >= size) {
            return false;
        }
        if (d.x() < 0 || d.x() >= size || d.y() < 0 || d.y() >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
