public class OutOfBoundaryRule implements Rule {

    public OutOfBoundaryRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        return sourceX >= 0 && sourceX < size &&
               sourceY >= 0 && sourceY < size &&
               destX >= 0 && destX < size &&
               destY >= 0 && destY < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
