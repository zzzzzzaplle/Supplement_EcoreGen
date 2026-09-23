public class KnightBlockRule implements Rule {
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x() - sx;
        int dy = move.getDestination().y() - sy;
        int bx, by;
        if (Math.abs(dx) == 2) {
            bx = (sx + move.getDestination().x()) / 2;
            by = sy;
        } else {
            bx = sx;
            by = (sy + move.getDestination().y()) / 2;
        }
        Place blockPlace = new Place(bx, by);
        Piece blockPiece = game.getPiece(blockPlace);
        return blockPiece == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
