public class OutOfBoundaryRule implements Rule {
    private int boardSize;

    public OutOfBoundaryRule(int boardSize) {
        this.boardSize = boardSize;
    }

    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        return source.x() >= 0 && source.x() < boardSize && source.y() >= 0 && source.y() < boardSize
                && destination.x() >= 0 && destination.x() < boardSize && destination.y() >= 0 && destination.y() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
