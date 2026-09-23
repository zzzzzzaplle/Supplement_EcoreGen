import java.util.ArrayList;
import java.util.Arrays;

public abstract class Piece implements Cloneable {
    private Player player;

    public Piece() {
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public abstract char getLabel();

    public Move[] getAvailableMoves(Game game, Place source) {
        ArrayList<Move> candidates = generateCandidateMoves(game, source);
        ArrayList<Move> validMoves = new ArrayList<>();
        for (Move move : candidates) {
            if (isValidMove(game, move)) {
                validMoves.add(move);
            }
        }
        return validMoves.toArray(new Move[0]);
    }

    private ArrayList<Move> generateCandidateMoves(Game game, Place source) {
        ArrayList<Move> candidates = new ArrayList<>();
        int size = game.getConfiguration().getSize();
        int[][] offsets = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] offset : offsets) {
            int nx = source.x() + offset[0];
            int ny = source.y() + offset[1];
            if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                candidates.add(new Move(source, new Place(nx, ny)));
            }
        }
        // Archer orthogonal moves
        int[] dirs = {-1, 1};
        for (int dx : dirs) {
            for (int dist = 1; dist < size; dist++) {
                int nx = source.x() + dx * dist;
                if (nx >= 0 && nx < size) {
                    candidates.add(new Move(source, new Place(nx, source.y())));
                }
            }
        }
        for (int dy : dirs) {
            for (int dist = 1; dist < size; dist++) {
                int ny = source.y() + dy * dist;
                if (ny >= 0 && ny < size) {
                    candidates.add(new Move(source, new Place(source.x(), ny)));
                }
            }
        }
        return candidates;
    }

    private boolean isValidMove(Game game, Move move) {
        Rule[] rules = {
            new VacantRule(),
            new NilMoveRule(),
            new OutOfBoundaryRule(),
            new OccupiedRule(),
            new KnightMoveRule(),
            new KnightBlockRule(),
            new ArcherMoveRule(),
            new FirstNMovesProtectionRule()
        };
        for (Rule rule : rules) {
            if (!rule.validate(game, move)) {
                return false;
            }
        }
        return true;
    }
}
