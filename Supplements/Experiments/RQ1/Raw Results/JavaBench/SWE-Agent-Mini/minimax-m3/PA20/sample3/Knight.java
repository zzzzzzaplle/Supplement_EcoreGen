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
        if (game == null || source == null) {
            return new Move[0];
        }
        int size = game.getConfiguration().getSize();
        int x = source.x();
        int y = source.y();
        int[][] offsets = new int[][]{
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        List<Move> moves = new ArrayList<>();
        for (int[] off : offsets) {
            int nx = x + off[0];
            int ny = y + off[1];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                continue;
            }
            Place dest = new Place(nx, ny);
            moves.add(new Move(source, dest));
        }
        return moves.toArray(new Move[0]);
    }
}
