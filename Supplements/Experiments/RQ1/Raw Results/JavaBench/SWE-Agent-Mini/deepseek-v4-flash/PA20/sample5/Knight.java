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

        // All L-shaped moves: (2,1), (2,-1), (-2,1), (-2,-1), (1,2), (1,-2), (-1,2), (-1,-2)
        int[][] offsets = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] offset : offsets) {
            int dx = offset[0];
            int dy = offset[1];
            int destX = x + dx;
            int destY = y + dy;

            Place destination = new Place(destX, destY);

            // Check boundary
            if (destX < 0 || destX >= game.getConfiguration().getSize() ||
                destY < 0 || destY >= game.getConfiguration().getSize()) {
                continue;
            }

            // Check blocking
            int blockX, blockY;
            if (Math.abs(dx) == 2) {
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

            moves.add(new Move(source, destination));
        }

        return moves.toArray(new Move[0]);
    }
}
