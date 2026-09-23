import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<Move>();
        int size = game.getConfiguration().getSize();
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            for (int step = 1; step < size; step++) {
                int nx = source.x() + d[0] * step;
                int ny = source.y() + d[1] * step;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }
        return moves.toArray(new Move[0]);
    }
}
