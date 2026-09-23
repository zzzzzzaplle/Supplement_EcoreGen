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
        int[][] directions = new int[][] {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        for (int[] dir : directions) {
            int step = 1;
            while (true) {
                int nx = source.x() + dir[0] * step;
                int ny = source.y() + dir[1] * step;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                Place dest = new Place(nx, ny);
                moves.add(new Move(source, dest));
                step++;
            }
        }
        return moves.toArray(new Move[0]);
    }

    @Override
    public Archer clone() throws CloneNotSupportedException {
        return (Archer) super.clone();
    }
}
