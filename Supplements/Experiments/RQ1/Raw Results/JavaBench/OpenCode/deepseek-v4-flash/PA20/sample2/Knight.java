import java.util.ArrayList;

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
        ArrayList<Move> moves = new ArrayList<Move>();
        int[][] offsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] offset : offsets) {
            int nx = source.x() + offset[0];
            int ny = source.y() + offset[1];
            Place dest = new Place(nx, ny);
            if (nx >= 0 && nx < game.getConfiguration().getSize() &&
                    ny >= 0 && ny < game.getConfiguration().getSize()) {
                Move move = new Move(source, dest);
                KnightMoveRule moveRule = new KnightMoveRule();
                KnightBlockRule blockRule = new KnightBlockRule();
                OccupiedRule occRule = new OccupiedRule();
                if (moveRule.validate(game, move) && blockRule.validate(game, move) && occRule.validate(game, move)) {
                    moves.add(move);
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
