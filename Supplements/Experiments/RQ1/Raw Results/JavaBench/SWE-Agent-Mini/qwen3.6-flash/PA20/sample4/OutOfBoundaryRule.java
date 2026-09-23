public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place destination = move.getDestination();
        return source.x() >= 0 && source.x() < size && source.y() >= 0 && source.y() < size &&
               destination.x() >= 0 && destination.x() < size && destination.y() >= 0 && destination.y() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
