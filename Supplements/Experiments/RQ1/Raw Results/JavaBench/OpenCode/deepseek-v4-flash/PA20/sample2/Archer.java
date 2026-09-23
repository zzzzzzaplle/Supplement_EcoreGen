import java.util.ArrayList;

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
        ArrayList<Move> moves = new ArrayList<Move>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int nx = source.x() + dir[0];
            int ny = source.y() + dir[1];
            while (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                Place dest = new Place(nx, ny);
                Move move = new Move(source, dest);
                ArcherMoveRule rule = new ArcherMoveRule();
                OccupiedRule occRule = new OccupiedRule();
                if (rule.validate(game, move) && occRule.validate(game, move)) {
                    moves.add(move);
                }
                nx += dir[0];
                ny += dir[1];
            }
        }
        return moves.toArray(new Move[0]);
    }
}
