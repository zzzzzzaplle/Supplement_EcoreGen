import java.util.*;

public class Knight extends Piece implements Cloneable {
    private KnightBlockRule blockRule;
    private KnightMoveRule moveRule;
    
    public char getLabel() {
        return 'K';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int offsets[][] = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        List<Move> availableMoves = new ArrayList<Move>();
        for (int[] offset : offsets) {
            int destX = source.x() + offset[0];
            int destY = source.y() + offset[1];

            if (destX < 0 || destX >= game.configuration.getSize() ||
                    destY < 0 || destY >= game.configuration.getSize()) {
                continue;
            }
            Place dest = new Place(destX, destY);

            Piece destPiece = game.getPiece(dest);
            Piece sourcePiece = game.getPiece(source);

            if (destPiece != null && destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                continue;
            }

            Move move = new Move(source, dest);
            if (game.validateMove(move, this)) {
                availableMoves.add(move);
            }
        }
        return availableMoves.toArray(new Move[0]);
    }
    
    public Knight() {
        this.blockRule = new KnightBlockRule();
        this.moveRule = new KnightMoveRule();
    }
}
