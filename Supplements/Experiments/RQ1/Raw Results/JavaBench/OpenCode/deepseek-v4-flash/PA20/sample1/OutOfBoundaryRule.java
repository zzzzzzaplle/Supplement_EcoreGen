public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dst = move.getDestination();
        return src.x() >= 0 && src.x() < size && src.y() >= 0 && src.y() < size &&
                dst.x() >= 0 && dst.x() < size && dst.y() >= 0 && dst.y() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
