import java.util.ArrayList;

public class Archer extends Piece {
    public Archer() {}

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    protected ArrayList<Move> computeMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();

        // Archer can move orthogonally in all 4 directions
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] d : directions) {
            for (int dist = 1; dist < size; dist++) {
                int nx = source.x() + d[0] * dist;
                int ny = source.y() + d[1] * dist;

                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }

                Piece target = game.getPiece(nx, ny);
                if (target == null) {
                    moves.add(new Move(source, new Place(nx, ny)));
                } else {
                    // Found a piece - for capturing with Archer, we need to continue
                    // to find if there's another piece behind it (the target)
                    break;
                }
            }
        }

        return moves;
    }
}
