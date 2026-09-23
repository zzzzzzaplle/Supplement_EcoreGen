import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<Move>();
        Configuration configuration = game.getConfiguration();
        int size = configuration.getSize();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        for (int[] dir : directions) {
            for (int step = 1; step < size; step++) {
                int destX = source.x() + dir[0] * step;
                int destY = source.y() + dir[1] * step;
                if (destX < 0 || destX >= size || destY < 0 || destY >= size) {
                    break;
                }
                Place dest = new Place(destX, destY);
                Piece middle = game.getPiece(destX, destY);
                moves.add(new Move(source, dest));
                if (middle != null) {
                    break;
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
