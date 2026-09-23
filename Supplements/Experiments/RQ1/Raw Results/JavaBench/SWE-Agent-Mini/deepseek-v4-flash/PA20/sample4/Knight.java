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
        int x = source.getX();
        int y = source.getY();

        // All L-shaped moves: (dx, dy) pairs for the 2-step direction
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] move : knightMoves) {
            int dx = move[0];
            int dy = move[1];
            int destX = x + dx;
            int destY = y + dy;

            Place dest = new Place(destX, destY);
            Move candidate = new Move(source, dest);

            // Apply global rules and piece-specific rules
            Rule[] rules = getRules(game);
            boolean valid = true;
            for (Rule rule : rules) {
                if (!rule.validate(game, candidate)) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                moves.add(candidate);
            }
        }

        return moves.toArray(new Move[0]);
    }

    private Rule[] getRules(Game game) {
        List<Rule> rules = new ArrayList<>();
        rules.add(new OutOfBoundaryRule());
        rules.add(new VacantRule());
        rules.add(new NilMoveRule());
        rules.add(new OccupiedRule());
        rules.add(new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()));
        rules.add(new KnightMoveRule());
        rules.add(new KnightBlockRule());
        return rules.toArray(new Rule[0]);
    }
}
