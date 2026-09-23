class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getSize();
        return move.getSource().x() >= 0 && move.getSource().x() < size &&
               move.getSource().y() >= 0 && move.getSource().y() < size &&
               move.getDestination().x() >= 0 && move.getDestination().x() < size &&
               move.getDestination().y() >= 0 && move.getDestination().y() < size;
    }

public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
