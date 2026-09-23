import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        
        // Orthogonal directions: up, down, left, right
        int[] dx = {0, 0, -1, 1};
        int[] dy = {1, -1, 0, 0};
        
        for (int dir = 0; dir < 4; dir++) {
            int piecesBetween = 0;
            int stepX = source.x() + dx[dir];
            int stepY = source.y() + dy[dir];
            
            while (stepX >= 0 && stepX < size && stepY >= 0 && stepY < size) {
                Piece currentPiece = game.getPiece(stepX, stepY);
                
                if (piecesBetween == 0) {
                    // Non-capturing move - path must be clear
                    if (currentPiece == null) {
                        moves.add(new Move(source, new Place(stepX, stepY)));
                    } else {
                        piecesBetween++;
                    }
                } else if (piecesBetween == 1) {
                    // Capturing move - exactly one piece between
                    if (currentPiece != null) {
                        if (!currentPiece.getPlayer().equals(this.getPlayer())) {
                            moves.add(new Move(source, new Place(stepX, stepY)));
                        }
                        break; // can't go past this piece
                    }
                } else {
                    break;
                }
                
                stepX += dx[dir];
                stepY += dy[dir];
            }
        }
        
        return moves.toArray(new Move[0]);
    }
}
