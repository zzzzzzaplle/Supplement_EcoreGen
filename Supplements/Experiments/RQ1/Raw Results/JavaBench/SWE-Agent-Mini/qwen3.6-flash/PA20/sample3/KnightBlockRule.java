public class KnightBlockRule implements Rule {
    
    public KnightBlockRule() {
    }
    
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        
        Place source = move.getSource();
        Place destination = move.getDestination();
        
        int sourceX = source.x();
        int sourceY = source.y();
        int destX = destination.x();
        int destY = destination.y();
        
        int blockX, blockY;
        
        if (Math.abs(destX - sourceX) == 2) {
            blockX = (sourceX + destX) / 2;
            blockY = sourceY;
        } else {
            blockX = sourceX;
            blockY = (sourceY + destY) / 2;
        }
        
        return game.getPiece(blockX, blockY) == null;
    }
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
