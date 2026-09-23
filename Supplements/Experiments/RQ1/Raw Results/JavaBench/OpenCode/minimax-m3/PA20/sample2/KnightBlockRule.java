public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        int blockX;
        int blockY;
        if (Math.abs(dx) == 2) {
            blockX = (source.x() + destination.x()) / 2;
            blockY = source.y();
        } else if (Math.abs(dy) == 2) {
            blockX = source.x();
            blockY = (source.y() + destination.y()) / 2;
        } else {
            return true;
        }
        Piece blocker = game.getPiece(blockX, blockY);
        return blocker == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
