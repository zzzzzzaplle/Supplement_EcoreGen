public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        if (srcX < 0 || srcX >= size || srcY < 0 || srcY >= size ||
            destX < 0 || destX >= size || destY < 0 || destY >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
