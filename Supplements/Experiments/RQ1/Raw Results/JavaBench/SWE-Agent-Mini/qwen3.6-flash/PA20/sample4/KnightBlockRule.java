public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        Place blockPlace;
        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            blockPlace = new Place(source.x() + dx / 2, source.y());
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            blockPlace = new Place(source.x(), source.y() + dy / 2);
        } else {
            return true;
        }
        Piece blockingPiece = game.getPiece(blockPlace);
        return blockingPiece == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
