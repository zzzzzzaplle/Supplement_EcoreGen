public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Knight)) {
            return true;
        }
        int size = game.getConfiguration().getSize();
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int absDx = Math.abs(dx - sx);
        int absDy = Math.abs(dy - sy);
        int blockX;
        int blockY;
        if (absDx == 2 && absDy == 1) {
            blockX = (sx + dx) / 2;
            blockY = sy;
        } else if (absDx == 1 && absDy == 2) {
            blockX = sx;
            blockY = (sy + dy) / 2;
        } else {
            return false;
        }
        if (blockX < 0 || blockX >= size || blockY < 0 || blockY >= size) {
            return false;
        }
        return game.getPiece(new Place(blockX, blockY)) == null;
    }

    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
