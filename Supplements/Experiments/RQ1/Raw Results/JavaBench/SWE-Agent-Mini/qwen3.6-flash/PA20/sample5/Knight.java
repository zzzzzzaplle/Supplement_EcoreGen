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

    public Move[] getAvailableMoves(Game game, Place source) {
        int[] dx = {-2, -2, -1, -1, 1, 1, 2, 2};
        int[] dy = {-1, 1, -2, 2, -2, 2, -1, 1};
        int[] bdx = {-1, -1, 0, 0, 0, 0, 1, 1};
        int[] bdy = {0, 0, -1, 1, -1, 1, 0, 0};

        List<Move> candidates = new ArrayList<>();
        int size = game.getConfiguration().getSize();

        for (int i = 0; i < 8; i++) {
            int nx = source.x() + dx[i];
            int ny = source.y() + dy[i];

            if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                continue;
            }

            // Check blocking
            int midX = source.x() + bdx[i];
            int midY = source.y() + bdy[i];

            Piece blockingPiece = game.getPiece(midX, midY);
            if (blockingPiece != null) {
                continue;
            }

            Piece destPiece = game.getPiece(nx, ny);
            if (destPiece != null && destPiece.getPlayer().equals(this.getPlayer())) {
                continue;
            }

            candidates.add(new Move(source, new Place(nx, ny)));
        }

        return candidates.toArray(new Move[0]);
    }
}
