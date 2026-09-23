public class KnightBlockRule implements Rule {

    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().getX() - move.getSource().getX());
        int dy = Math.abs(move.getDestination().getY() - move.getSource().getY());
        
        int blockX, blockY;
        if (dx == 2 && dy == 1) {
            blockX = (move.getSource().getX() + move.getDestination().getX()) / 2;
            blockY = move.getSource().getY();
        } else if (dx == 1 && dy == 2) {
            blockX = move.getSource().getX();
            blockY = (move.getSource().getY() + move.getDestination().getY()) / 2;
        } else {
            return true; // Not a valid knight move, skip blocking check
        }
        
        Place blockPlace = new Place(blockX, blockY);
        return game.getPiece(blockPlace) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
