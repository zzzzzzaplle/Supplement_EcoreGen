public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        
        Place source = move.getSource();
        Place dest = move.getDestination();
        
        int dx = dest.x() - source.x();
        int dy = dest.y() - source.y();
        
        int blockingX, blockingY;
        
        if (Math.abs(dx) == 2) {
            blockingX = source.x() + (dx / 2);
            blockingY = source.y();
        } else {
            blockingX = source.x();
            blockingY = source.y() + (dy / 2);
        }
        
        Place blockingPlace = new Place(blockingX, blockingY);
        return game.getPiece(blockingPlace) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
    
    public KnightBlockRule() {
    }
}
