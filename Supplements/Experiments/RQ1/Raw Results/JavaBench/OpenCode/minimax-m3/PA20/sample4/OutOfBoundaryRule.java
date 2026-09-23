public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        int size = game.getConfiguration().getSize();
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        return sx >= 0 && sx < size && sy >= 0 && sy < size
                && dx >= 0 && dx < size && dy >= 0 && dy < size;
    }

    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
