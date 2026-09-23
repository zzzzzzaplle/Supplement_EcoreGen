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
        if (game == null || source == null) {
            return moves.toArray(new Move[0]);
        }
        int size = game.getConfiguration().getSize();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        for (int[] dir : directions) {
            for (int step = 1; step < size; step++) {
                int newX = source.x() + dir[0] * step;
                int newY = source.y() + dir[1] * step;
                if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
                    break;
                }
                Place dest = new Place(newX, newY);
                Piece target = game.getPiece(dest);
                if (target == null) {
                    moves.add(new Move(source, dest));
                } else {
                    int screenCount = 0;
                    for (int s = 1; s < step; s++) {
                        Place screenPlace = new Place(source.x() + dir[0] * s,
                                source.y() + dir[1] * s);
                        if (game.getPiece(screenPlace) != null) {
                            screenCount++;
                        }
                    }
                    if (screenCount == 1) {
                        moves.add(new Move(source, dest));
                    }
                    break;
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
