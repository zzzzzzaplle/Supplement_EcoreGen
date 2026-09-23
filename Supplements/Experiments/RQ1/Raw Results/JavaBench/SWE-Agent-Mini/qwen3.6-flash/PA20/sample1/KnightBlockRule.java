public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();

        int blockingX, blockingY;

        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            blockingX = (source.x() + destination.x()) / 2;
            blockingY = source.y();
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            blockingX = source.x();
            blockingY = (source.y() + destination.y()) / 2;
        } else {
            return true;
        }

        Piece blockingPiece = game.getPiece(blockingX, blockingY);
        if (blockingPiece != null) {
            return false;
        }

        return true;
    }
}
