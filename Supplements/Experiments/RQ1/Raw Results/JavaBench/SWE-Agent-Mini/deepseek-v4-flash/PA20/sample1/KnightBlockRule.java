public class KnightBlockRule implements Rule {
    
    public KnightBlockRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        
        int blockX, blockY;
        if (Math.abs(dx) == 2) {
            blockX = (move.getSource().x() + move.getDestination().x()) / 2;
            blockY = move.getSource().y();
        } else {
            blockX = move.getSource().x();
            blockY = (move.getSource().y() + move.getDestination().y()) / 2;
        }
        
        Piece blockPiece = game.getPiece(blockX, blockY);
        if (blockPiece != null) {
            return false;
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}
