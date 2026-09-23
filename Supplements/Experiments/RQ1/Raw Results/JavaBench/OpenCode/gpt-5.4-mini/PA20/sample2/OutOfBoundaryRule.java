public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int size = game.getConfiguration().getSize();
        return source.getX() >= 0 && source.getX() < size && source.getY() >= 0 && source.getY() < size &&
                destination.getX() >= 0 && destination.getX() < size && destination.getY() >= 0 && destination.getY() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
