public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place destination = move.getDestination();
        
        if (source.getX() < 0 || source.getX() >= size || source.getY() < 0 || source.getY() >= size) {
            return false;
        }
        if (destination.getX() < 0 || destination.getX() >= size || destination.getY() < 0 || destination.getY() >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
