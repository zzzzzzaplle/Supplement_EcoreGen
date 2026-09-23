public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int diffX = Math.abs(dx - sx);
        int diffY = Math.abs(dy - sy);
        Place blocker;
        if (diffX == 2 && diffY == 1) {
            blocker = new Place((sx + dx) / 2, sy);
        } else if (diffX == 1 && diffY == 2) {
            blocker = new Place(sx, (sy + dy) / 2);
        } else {
            return true;
        }
        return game.getPiece(blocker) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
