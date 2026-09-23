public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        if (!((absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2))) {
            return true;
        }
        int blockX;
        int blockY;
        if (absDx == 2) {
            blockX = (source.x() + destination.x()) / 2;
            blockY = source.y();
        } else {
            blockX = source.x();
            blockY = (source.y() + destination.y()) / 2;
        }
        return game.getPiece(blockX, blockY) == null;
    }

public String getDescription() {
        return "knight is blocked by another piece";
    }
}
