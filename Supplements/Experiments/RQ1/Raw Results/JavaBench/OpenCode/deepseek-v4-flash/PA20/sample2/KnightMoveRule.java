public class KnightMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        Place src = move.getSource();
        Place dst = move.getDestination();
        int dx = Math.abs(src.x() - dst.x());
        int dy = Math.abs(src.y() - dst.y());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
