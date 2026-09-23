public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        Place src = move.getSource();
        Place dst = move.getDestination();
        int dx = Math.abs(src.x() - dst.x());
        int dy = Math.abs(src.y() - dst.y());
        if (dx == 2 && dy == 1) {
            int blockX = (src.x() + dst.x()) / 2;
            int blockY = src.y();
            return game.getPiece(blockX, blockY) == null;
        } else if (dx == 1 && dy == 2) {
            int blockX = src.x();
            int blockY = (src.y() + dst.y()) / 2;
            return game.getPiece(blockX, blockY) == null;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
