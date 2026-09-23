public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dst = move.getDestination();
        if (src.getX() < 0 || src.getX() >= size || src.getY() < 0 || src.getY() >= size) {
            return false;
        }
        if (dst.getX() < 0 || dst.getX() >= size || dst.getY() < 0 || dst.getY() >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
