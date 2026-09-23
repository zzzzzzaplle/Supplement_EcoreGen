import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight() {
    }

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] deltas = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] d : deltas) {
            int nx = source.x() + d[0];
            int ny = source.y() + d[1];
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}
