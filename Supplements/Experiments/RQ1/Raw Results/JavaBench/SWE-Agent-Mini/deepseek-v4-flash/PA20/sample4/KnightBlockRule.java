public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int srcX = move.getSource().getX();
        int srcY = move.getSource().getY();
        int dstX = move.getDestination().getX();
        int dstY = move.getDestination().getY();
        int dx = Math.abs(dstX - srcX);
        int dy = Math.abs(dstY - srcY);

        // Find blocking square
        int blockX;
        int blockY;
        if (dx == 2 && dy == 1) {
            // Moves 2 horizontally, 1 vertically
            blockX = (srcX + dstX) / 2;
            blockY = srcY;
        } else if (dx == 1 && dy == 2) {
            // Moves 1 horizontally, 2 vertically
            blockX = srcX;
            blockY = (srcY + dstY) / 2;
        } else {
            // Not a valid knight move, but let KnightMoveRule handle that
            return true;
        }

        Place blockPlace = new Place(blockX, blockY);
        return game.getPiece(blockPlace) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
