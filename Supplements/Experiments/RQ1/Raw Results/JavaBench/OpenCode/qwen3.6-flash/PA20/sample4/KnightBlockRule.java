public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int blockX, blockY;

        if (destX - sourceX == 2 || destX - sourceX == -2) {
            blockX = (sourceX + destX) / 2;
            blockY = sourceY;
        } else {
            blockX = sourceX;
            blockY = (sourceY + destY) / 2;
        }

        return game.getPiece(blockX, blockY) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
