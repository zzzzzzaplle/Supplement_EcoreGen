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
        java.util.ArrayList<Move> moves = new java.util.ArrayList<>();
        int size = game.getConfiguration().getSize();
        int sx = source.x();
        int sy = source.y();

        // Four orthogonal directions: right, left, up, down
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        for (int d = 0; d < 4; d++) {
            java.util.ArrayList<Place> pathPieces = new java.util.ArrayList<>();
            java.util.ArrayList<Integer> pathDistances = new java.util.ArrayList<>();

            for (int s = 1; ; s++) {
                int nx = sx + dx[d] * s;
                int ny = sy + dy[d] * s;
                if (nx < 0 || nx >= size || ny < 0 || ny >= size) break;

                Place p = new Place(nx, ny);
                Piece piece = game.getPiece(p);
                if (piece != null) {
                    pathPieces.add(p);
                    pathDistances.add(s);
                }
            }

            // Non-capturing moves: path must be clear (no pieces in between)
            if (pathPieces.isEmpty()) {
                // All squares in this direction are empty
                for (int s = 1; ; s++) {
                    int nx = sx + dx[d] * s;
                    int ny = sy + dy[d] * s;
                    if (nx < 0 || nx >= size || ny < 0 || ny >= size) break;
                    moves.add(new Move(source, new Place(nx, ny)));
                }
            } else {
                // Squares before first piece are non-capturing moves
                int firstPieceDist = pathDistances.get(0);
                for (int s = 1; s < firstPieceDist; s++) {
                    int nx = sx + dx[d] * s;
                    int ny = sy + dy[d] * s;
                    moves.add(new Move(source, new Place(nx, ny)));
                }
                // If there's exactly one piece between, the next piece (if any) is a capturing target
                if (pathPieces.size() >= 2) {
                    // The first piece is the screen, the second piece is the capture target
                    Place captureTarget = pathPieces.get(1);
                    moves.add(new Move(source, captureTarget));
                }
            }
        }
        return moves.toArray(new Move[0]);
    }
}
