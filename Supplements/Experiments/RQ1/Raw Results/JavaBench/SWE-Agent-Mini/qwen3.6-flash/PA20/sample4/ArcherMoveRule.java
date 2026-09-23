public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place source = move.getSource();
        Place destination = move.getDestination();
        int size = game.getConfiguration().getSize();

        // Must be orthogonal move
        if (source.x() != destination.x() && source.y() != destination.y()) {
            return false;
        }

        int dx = Integer.compare(destination.x(), source.x());
        int dy = Integer.compare(destination.y(), source.y());

        int piecesInBetween = 0;
        int x = source.x() + dx;
        int y = source.y() + dy;
        while (x != destination.x() || y != destination.y()) {
            Piece piece = game.getPiece(x, y);
            if (piece != null) {
                piecesInBetween++;
            }
            x += dx;
            y += dy;
        }

        Piece destPiece = game.getPiece(destination);
        if (destPiece != null) {
            // Capturing move: must have exactly one piece in between
            return piecesInBetween == 1;
        } else {
            // Non-capturing move: must have no pieces in between
            return piecesInBetween == 0;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
