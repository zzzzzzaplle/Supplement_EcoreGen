import java.util.ArrayList;

public class Knight extends Piece {
    public char getLabel() {
        return 'K';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> candidates = new ArrayList<>();
        int size = game.configurationGetSize();
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};

        for (int i = 0; i < 8; i++) {
            int nx = source.x() + dx[i];
            int ny = source.y() + dy[i];
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                Place dest = new Place(nx, ny);
                candidates.add(new Move(source, dest));
            }
        }
        return candidates.toArray(new Move[0]);
    }
}
