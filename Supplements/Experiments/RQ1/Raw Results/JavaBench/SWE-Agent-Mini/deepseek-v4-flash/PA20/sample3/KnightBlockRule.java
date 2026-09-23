public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = move.getDestination().getX() - move.getSource().getX();
        int dy = move.getDestination().getY() - move.getSource().getY();
        int blockX, blockY;
        if (Math.abs(dx) == 2) {
            blockX = (move.getSource().getX() + move.getDestination().getX()) / 2;
            blockY = move.getSource().getY();
        } else {
            blockX = move.getSource().getX();
            blockY = (move.getSource().getY() + move.getDestination().getY()) / 2;
        }
        Place blockPlace = new Place(blockX, blockY);
        return game.getPiece(blockPlace) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
