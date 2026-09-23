import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight() {
        super();
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<Move>();
        Configuration configuration = game.getConfiguration();
        int size = configuration.getSize();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] offset : offsets) {
            int destX = source.x() + offset[0];
            int destY = source.y() + offset[1];
            if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                continue;
            }
            Place dest = new Place(destX, destY);
            int blockX;
            int blockY;
            if (Math.abs(offset[0]) == 2) {
                blockX = (source.x() + destX) / 2;
                blockY = source.y();
            } else {
                blockX = source.x();
                blockY = (source.y() + destY) / 2;
            }
            Piece blocker = game.getPiece(blockX, blockY);
            if (blocker != null) {
                continue;
            }
            moves.add(new Move(source, dest));
        }
        return moves.toArray(new Move[0]);
    }
}
