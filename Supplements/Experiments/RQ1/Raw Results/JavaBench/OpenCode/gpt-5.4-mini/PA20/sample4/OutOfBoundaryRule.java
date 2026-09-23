public class OutOfBoundaryRule implements Rule {
    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        return move.getSource().getX() >= 0 && move.getSource().getY() >= 0 && move.getDestination().getX() >= 0 && move.getDestination().getY() >= 0 && move.getSource().getX() < size && move.getSource().getY() < size && move.getDestination().getX() < size && move.getDestination().getY() < size;
    }

public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
