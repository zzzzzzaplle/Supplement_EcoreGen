import java.util.ArrayList;

public class Knight extends Piece {
    public Knight() {}

    public Knight(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'K';
    }

    protected ArrayList<Move> computeMoves(Game game, Place source) {
        ArrayList<Move> moves = new ArrayList<>();

        // Knight moves in L shape: (2,1) and (1,2) in all 8 directions
        int[][] directions = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        int size = game.getConfiguration().getSize();

        for (int[] d : directions) {
            int nx = source.x() + d[0];
            int ny = source.y() + d[1];

            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                moves.add(new Move(source, new Place(nx, ny)));
            }
        }

        return moves;
    }
}
