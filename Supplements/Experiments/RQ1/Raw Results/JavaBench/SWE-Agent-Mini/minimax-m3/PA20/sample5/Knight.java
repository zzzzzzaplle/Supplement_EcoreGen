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
        int[][] deltas = new int[][] {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        int size = game.getConfiguration().getSize();
        for (int[] d : deltas) {
            int nx = source.x() + d[0];
            int ny = source.y() + d[1];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                continue;
            }
            Place dest = new Place(nx, ny);
            Move m = new Move(source, dest);
            moves.add(m);
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Knight clone() throws CloneNotSupportedException {
        return (Knight) super.clone();
    }
}
