public class KnightMoveRule implements Rule {
    public KnightMoveRule() {
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int dx = Math.abs(destination.x() - source.x());
        int dy = Math.abs(destination.y() - source.y());

        boolean isKnightMove = (dx == 2 && dy == 1) || (dx == 1 && dy == 2);

        return isKnightMove;
    }
}
