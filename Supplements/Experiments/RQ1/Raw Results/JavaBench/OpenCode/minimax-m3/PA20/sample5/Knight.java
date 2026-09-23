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
        if (game == null || source == null) {
            return moves.toArray(new Move[0]);
        }
        int size = game.getConfiguration().getSize();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] offset : offsets) {
            int newX = source.x() + offset[0];
            int newY = source.y() + offset[1];
            if (newX >= 0 && newX < size && newY >= 0 && newY < size) {
                moves.add(new Move(source, new Place(newX, newY)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}
