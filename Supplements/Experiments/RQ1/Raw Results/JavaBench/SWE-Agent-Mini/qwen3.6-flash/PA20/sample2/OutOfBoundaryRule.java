public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.configuration.getSize();
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        if (source.x() < 0 || source.y() < 0 || source.x() >= size || source.y() >= size) {
            return false;
        }
        if (dest.x() < 0 || dest.y() < 0 || dest.x() >= size || dest.y() >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
    
    public OutOfBoundaryRule() {
    }
}
