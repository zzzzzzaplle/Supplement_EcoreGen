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
        List<Move> moves = new ArrayList<Move>();
        int size = game.getConfiguration().getSize();
        int[] dx = {2, 2, -2, -2, 1, 1, -1, -1};
        int[] dy = {1, -1, 1, -1, 2, -2, 2, -2};
        for (int i = 0; i < 8; i++) {
            int nx = source.x() + dx[i];
            int ny = source.y() + dy[i];
            if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                continue;
            }
            moves.add(new Move(source, new Place(nx, ny)));
        }
        return moves.toArray(new Move[0]);
    }
}
