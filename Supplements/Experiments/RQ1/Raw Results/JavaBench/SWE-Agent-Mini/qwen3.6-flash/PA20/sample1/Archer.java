public class Archer extends Piece implements Cloneable {
    public Archer() {
        super();
    }

    public Archer(Player player) {
        super(player);
    }

    public char getLabel() {
        return 'A';
    }

    public Move[] getAvailableMoves(Game game, Place source) {
        int size = game.getConfiguration().getSize();
        java.util.ArrayList<Move> moves = new java.util.ArrayList<>();

        // Four orthogonal directions
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int d = 0; d < 4; d++) {
            int count = 0;
            boolean hasPieceInPath = false;
            for (int step = 1; step < size; step++) {
                int newX = source.x() + dx[d] * step;
                int newY = source.y() + dy[d] * step;

                if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
                    break;
                }

                Place dest = new Place(newX, newY);
                Piece destPiece = game.getPiece(dest);

                if (step == 1 && destPiece != null) {
                    // First step has piece - can capture if it's enemy (exactly one piece as screen)
                    // The screen is at dest, and we need to capture beyond it
                    count++;
                    hasPieceInPath = true;
                } else if (hasPieceInPath && destPiece != null) {
                    // Second piece found - cannot jump two pieces, stop
                    break;
                } else if (hasPieceInPath && destPiece == null) {
                    // Empty square after screen - this is a valid capture
                    moves.add(new Move(source, dest));
                    break;
                } else if (!hasPieceInPath && destPiece == null) {
                    // No piece yet in path, empty square is a valid move
                    moves.add(new Move(source, dest));
                }
                // If we haven't found a piece yet and found empty, continue
            }
        }

        return moves.toArray(new Move[0]);
    }
}
