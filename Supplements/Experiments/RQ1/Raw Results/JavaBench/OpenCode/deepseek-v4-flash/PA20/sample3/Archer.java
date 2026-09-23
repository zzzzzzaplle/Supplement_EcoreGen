import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {
    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = source.x() + dx;
            int ny = source.y() + dy;
            while (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                Place dest = new Place(nx, ny);
                Move move = new Move(source, dest);
                boolean valid = true;
                if (!new OutOfBoundaryRule().validate(game, move)) {
                    valid = false;
                }
                if (!new NilMoveRule().validate(game, move)) {
                    valid = false;
                }
                if (!new OccupiedRule().validate(game, move)) {
                    valid = false;
                }
                if (!new ArcherMoveRule().validate(game, move)) {
                    valid = false;
                }
                if (valid) {
                    moves.add(move);
                }
                nx += dx;
                ny += dy;
            }
        }
        return moves.toArray(new Move[0]);
    }
}
