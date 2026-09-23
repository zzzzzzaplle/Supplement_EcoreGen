import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {
    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : dirs) {
            boolean screenSeen = false;
            for (int step = 1; step < size; step++) {
                int nx = source.x() + dir[0] * step;
                int ny = source.y() + dir[1] * step;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) {
                    break;
                }
                Piece target = game.getPiece(nx, ny);
                if (!screenSeen) {
                    if (target == null) {
                        moves.add(new Move(source, new Place(nx, ny)));
                    } else {
                        screenSeen = true;
                    }
                } else {
                    if (target != null) {
                        if (!target.getPlayer().equals(this.getPlayer())) {
                            moves.add(new Move(source, new Place(nx, ny)));
                        }
                        break;
                    }
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
