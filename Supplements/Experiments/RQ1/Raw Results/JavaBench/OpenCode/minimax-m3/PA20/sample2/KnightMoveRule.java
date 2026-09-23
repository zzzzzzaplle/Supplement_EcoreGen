public class KnightMoveRule implements Rule {

    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = Math.abs(destination.x() - source.x());
        int dy = Math.abs(destination.y() - source.y());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
