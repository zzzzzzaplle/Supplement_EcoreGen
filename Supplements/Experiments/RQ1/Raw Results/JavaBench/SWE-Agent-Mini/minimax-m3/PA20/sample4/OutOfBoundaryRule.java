public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dest = move.getDestination();
        return src.x() >= 0 && src.y() >= 0 && src.x() < size && src.y() < size
                && dest.x() >= 0 && dest.y() >= 0 && dest.x() < size && dest.y() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
