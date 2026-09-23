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
        int x = source.getX();
        int y = source.getY();
        int[][] offsets = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] offset : offsets) {
            int destX = x + offset[0];
            int destY = y + offset[1];
            Place destination = new Place(destX, destY);
            Move move = new Move(source, destination);
            // Check out of bounds
            int size = game.getConfiguration().getSize();
            if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                continue;
            }
            // Check blocking
            int blockX, blockY;
            if (Math.abs(offset[0]) == 2) {
                blockX = (x + destX) / 2;
                blockY = y;
            } else {
                blockX = x;
                blockY = (y + destY) / 2;
            }
            Place blockPlace = new Place(blockX, blockY);
            if (game.getPiece(blockPlace) != null) {
                continue;
            }
            // Check destination not friendly
            Piece destPiece = game.getPiece(destination);
            if (destPiece != null && destPiece.getPlayer().equals(this.getPlayer())) {
                continue;
            }
            moves.add(move);
        }
        return moves.toArray(new Move[0]);
    }
}
