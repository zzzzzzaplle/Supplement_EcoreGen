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
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        int size = game.getConfiguration().getSize();
        for (int[] off : offsets) {
            int dx = off[0];
            int dy = off[1];
            int nx = source.x() + dx;
            int ny = source.y() + dy;
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                Place dest = new Place(nx, ny);
                Move move = new Move(source, dest);
                boolean valid = true;
                Rule[] rules = {
                        new VacantRule(),
                        new NilMoveRule(),
                        new OutOfBoundaryRule(),
                        new OccupiedRule(),
                        new KnightMoveRule(),
                        new KnightBlockRule()
                };
                for (Rule rule : rules) {
                    if (!rule.validate(game, move)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    moves.add(move);
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
