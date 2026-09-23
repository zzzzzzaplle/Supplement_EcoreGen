import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int[] dx = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1, 2, -2, 2, -2};
        
        for (int i = 0; i < dx.length; i++) {
            int destX = source.x() + dx[i];
            int destY = source.y() + dy[i];
            
            Place dest = new Place(destX, destY);
            Move move = new Move(source, dest);
            
            if (destX >= 0 && destX < game.getConfiguration().getSize() &&
                destY >= 0 && destY < game.getConfiguration().getSize()) {
                // Check block square
                int blockX, blockY;
                if (Math.abs(dx[i]) == 2) {
                    blockX = (source.x() + destX) / 2;
                    blockY = source.y();
                } else {
                    blockX = source.x();
                    blockY = (source.y() + destY) / 2;
                }
                
                Piece blockPiece = game.getPiece(blockX, blockY);
                Piece destPiece = game.getPiece(dest);
                
                if (blockPiece == null) {
                    if (destPiece == null || !destPiece.getPlayer().equals(this.getPlayer())) {
                        moves.add(move);
                    }
                }
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}
