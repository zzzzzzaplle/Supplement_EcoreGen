public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place dest = move.getDestination();
        return source.x() >= 0 && source.x() < size &&
                source.y() >= 0 && source.y() < size &&
                dest.x() >= 0 && dest.x() < size &&
                dest.y() >= 0 && dest.y() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
