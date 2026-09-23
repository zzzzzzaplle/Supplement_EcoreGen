public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place src = move.getSource();
        Place dst = move.getDestination();
        int dx = Math.abs(src.x() - dst.x());
        int dy = Math.abs(src.y() - dst.y());
        if (dx != 0 && dy != 0) {
            return false;
        }
        int minX = Math.min(src.x(), dst.x());
        int maxX = Math.max(src.x(), dst.x());
        int minY = Math.min(src.y(), dst.y());
        int maxY = Math.max(src.y(), dst.y());
        int piecesBetween = 0;
        if (dx == 0) {
            for (int y = minY + 1; y < maxY; y++) {
                if (game.getPiece(src.x(), y) != null) {
                    piecesBetween++;
                }
            }
        } else {
            for (int x = minX + 1; x < maxX; x++) {
                if (game.getPiece(x, src.y()) != null) {
                    piecesBetween++;
                }
            }
        }
        if (game.getPiece(dst) == null) {
            return piecesBetween == 0;
        } else {
            return piecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
