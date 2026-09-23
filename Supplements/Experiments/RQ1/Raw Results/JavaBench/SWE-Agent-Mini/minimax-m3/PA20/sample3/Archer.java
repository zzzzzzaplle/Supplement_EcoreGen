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
        if (game == null || source == null) {
            return new Move[0];
        }
        int size = game.getConfiguration().getSize();
        int x = source.x();
        int y = source.y();
        List<Move> moves = new ArrayList<>();
        int[][] dirs = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            while (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                moves.add(new Move(source, new Place(nx, ny)));
                nx += d[0];
                ny += d[1];
            }
        }
        return moves.toArray(new Move[0]);
    }
}
