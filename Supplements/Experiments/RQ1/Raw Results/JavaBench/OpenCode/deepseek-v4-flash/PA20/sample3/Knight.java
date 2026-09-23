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
        int[][] offsets = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
        for (int[] offset : offsets) {
            int dx = offset[0];
            int dy = offset[1];
            int nx = source.x() + dx;
            int ny = source.y() + dy;
            Place dest = new Place(nx, ny);
            Move move = new Move(source, dest);
            boolean valid = true;
            if (!new OutOfBoundaryRule().validate(game, move)) {
                valid = false;
            }
            if (!new NilMoveRule().validate(game, move)) {
                valid = false;
            }
            if (!new KnightMoveRule().validate(game, move)) {
                valid = false;
            }
            if (!new KnightBlockRule().validate(game, move)) {
                valid = false;
            }
            if (!new OccupiedRule().validate(game, move)) {
                valid = false;
            }
            if (valid) {
                moves.add(move);
            }
        }
        return moves.toArray(new Move[0]);
    }
}
