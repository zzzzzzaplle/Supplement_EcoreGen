public class Knight extends Piece {
    
    public Knight() {
        super(null);
    }
    
    public Knight(Player player) {
        super(player);
    }
    
    public char getLabel() {
        return 'K';
    }
    
    public Move[] getAvailableMoves(Game game, Place source) {
        java.util.List<Move> moves = new java.util.ArrayList<>();
        
        // Knight moves in L-shape: 2 in one direction, 1 in orthogonal
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};
        
        int size = game.getConfiguration().getSize();
        
        for (int i = 0; i < dx.length; i++) {
            int destX = source.x() + dx[i];
            int destY = source.y() + dy[i];
            
            // Check boundary
            if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                continue;
            }
            
            // Check blocking (knight block rule)
            int blockX, blockY;
            if (Math.abs(dx[i]) == 2) {
                // Moving 2 in x direction, block at (source.x + dx[i]/2, source.y)
                blockX = source.x() + dx[i] / 2;
                blockY = source.y();
            } else {
                // Moving 2 in y direction, block at (source.x, source.y + dy[i]/2)
                blockX = source.x();
                blockY = source.y() + dy[i] / 2;
            }
            
            // Check if blocked
            if (game.getPiece(blockX, blockY) != null) {
                continue;
            }
            
            Place dest = new Place(destX, destY);
            Move candidate = new Move(source, dest);
            
            // Apply rules
            if (new VacantRule().validate(game, candidate) &&
                new NilMoveRule().validate(game, candidate) &&
                new OutOfBoundaryRule().validate(game, candidate) &&
                new OccupiedRule().validate(game, candidate) &&
                new KnightMoveRule().validate(game, candidate) &&
                new KnightBlockRule().validate(game, candidate)) {
                moves.add(candidate);
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}
