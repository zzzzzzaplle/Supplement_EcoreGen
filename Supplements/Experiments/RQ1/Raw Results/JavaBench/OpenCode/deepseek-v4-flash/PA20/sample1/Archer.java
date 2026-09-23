import java.util.ArrayList;
import java.util.List;

public class Archer extends Piece {

    public Archer() {
    }

    public Archer(Player player) {
        super(player);
    }

    @Override
    public char getLabel() {
        return 'A';
    }

    @Override
    public Move[] getAvailableMoves(Game game, Place source) {
        List<Move> moves = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = source.x() + dx;
            int y = source.y() + dy;
            boolean screened = false;

            while (x >= 0 && x < size && y >= 0 && y < size) {
                Place dest = new Place(x, y);
                Piece destPiece = game.getPiece(dest);

                if (!screened) {
                    if (destPiece == null) {
                        Move move = new Move(source, dest);
                        boolean valid = true;
                        Rule[] rules = {
                                new VacantRule(),
                                new NilMoveRule(),
                                new OutOfBoundaryRule(),
                                new OccupiedRule()
                        };
                        for (Rule rule : rules) {
                            if (!rule.validate(game, move)) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            moves.add(move);
                        }
                    } else {
                        screened = true;
                    }
                } else {
                    if (destPiece != null) {
                        Move move = new Move(source, dest);
                        boolean valid = true;
                        Rule[] rules = {
                                new VacantRule(),
                                new NilMoveRule(),
                                new OutOfBoundaryRule(),
                                new OccupiedRule()
                        };
                        for (Rule rule : rules) {
                            if (!rule.validate(game, move)) {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            moves.add(move);
                        }
                        break;
                    }
                }

                x += dx;
                y += dy;
            }
        }
        return moves.toArray(new Move[0]);
    }
}
