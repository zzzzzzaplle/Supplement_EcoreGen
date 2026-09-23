public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        Piece piece = game.getPiece(source);
        if (piece == null) {
            return true;
        }
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();

        int blockingX, blockingY;
        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            // moves 2 squares horizontally and 1 square vertically
            blockingX = (source.x() + destination.x()) / 2;
            blockingY = source.y();
        } else {
            // moves 1 square horizontally and 2 squares vertically
            blockingX = source.x();
            blockingY = (source.y() + destination.y()) / 2;
        }
        Piece blockingPiece = game.getPiece(blockingX, blockingY);
        return blockingPiece == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
