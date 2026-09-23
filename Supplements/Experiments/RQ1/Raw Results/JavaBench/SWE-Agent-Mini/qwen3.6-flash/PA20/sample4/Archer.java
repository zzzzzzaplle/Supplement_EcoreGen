public class Archer extends Piece implements Cloneable {
    public Archer() {
    }

    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        // Generate orthogonal candidate moves for Archer
        java.util.ArrayList<Move> candidates = new java.util.ArrayList<>();
        int size = game.getConfiguration().getSize();
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
        java.util.ArrayList<Move> validMoves = new java.util.ArrayList<>();
        for (Move move : candidates) {
            if (isValidMove(game, move)) {
                validMoves.add(move);
            }
        }
        return validMoves.toArray(new Move[0]);
    }

    private boolean isValidMove(Game game, Move move) {
        Rule[] rules = {
            new VacantRule(),
            new NilMoveRule(),
            new OutOfBoundaryRule(),
            new OccupiedRule(),
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

    @Override
    public Archer clone() throws CloneNotSupportedException {
        return (Archer) super.clone();
    }
}
