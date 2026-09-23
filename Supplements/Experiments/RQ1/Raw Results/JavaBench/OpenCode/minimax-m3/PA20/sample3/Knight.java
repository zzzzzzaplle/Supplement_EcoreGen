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
        List<Move> moves = new ArrayList<>();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        Rule[] rules = new Rule[]{
                new VacantRule(),
                new NilMoveRule(),
                new OutOfBoundaryRule(),
                new OccupiedRule(),
                new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()),
                new KnightMoveRule(),
                new KnightBlockRule()
        };
        for (int[] off : offsets) {
            Place destination = new Place(source.x() + off[0], source.y() + off[1]);
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
        return moves.toArray(new Move[0]);
    }
}
