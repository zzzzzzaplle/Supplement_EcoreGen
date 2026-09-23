public class KnightMoveRule implements Rule {
    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = Math.abs(source.x() - destination.x());
        int dy = Math.abs(source.y() - destination.y());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
