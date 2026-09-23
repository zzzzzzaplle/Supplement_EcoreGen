public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dest = move.getDestination();
        return src.getX() >= 0 && src.getX() < size && src.getY() >= 0 && src.getY() < size &&
               dest.getX() >= 0 && dest.getX() < size && dest.getY() >= 0 && dest.getY() < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
