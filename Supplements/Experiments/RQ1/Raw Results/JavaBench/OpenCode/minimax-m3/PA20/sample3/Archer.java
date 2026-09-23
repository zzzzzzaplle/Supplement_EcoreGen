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
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        Rule[] rules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
                new ArcherMoveRule()
        };
        for (int[] dir : directions) {
            for (int step = 1; step < size; step++) {
                int dx = source.x() + dir[0] * step;
                int dy = source.y() + dir[1] * step;
                if (dx < 0 || dx >= size || dy < 0 || dy >= size) {
                    break;
                }
                Place destination = new Place(dx, dy);
                Move move = new Move(source, destination);
                boolean valid = true;
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
