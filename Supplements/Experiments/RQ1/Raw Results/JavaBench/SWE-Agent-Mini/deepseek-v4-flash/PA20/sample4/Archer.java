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
        int x = source.getX();
        int y = source.getY();
        int size = game.getConfiguration().getSize();

        // Orthogonal directions: up, down, left, right
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int curX = x + dx;
            int curY = y + dy;

            while (curX >= 0 && curX < size && curY >= 0 && curY < size) {
                Place dest = new Place(curX, curY);
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
                curX += dx;
                curY += dy;
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
        rules.add(new ArcherMoveRule());
        return rules.toArray(new Rule[0]);
    }
}
