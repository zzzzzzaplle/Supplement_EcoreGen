public class ArcherMoveRule implements Rule {
    public ArcherMoveRule() {
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();

        if (!((dx == 0) != (dy == 0))) {
            return false;
        }

        int steps;
        int xInc, yInc;

        if (dx != 0) {
            steps = Math.abs(dx);
            xInc = dx > 0 ? 1 : -1;
            yInc = 0;
        } else {
            steps = Math.abs(dy);
            xInc = 0;
            yInc = dy > 0 ? 1 : -1;
        }

        int piecesInBetween = 0;
        for (int step = 1; step < steps; step++) {
            int checkX = source.x() + xInc * step;
            int checkY = source.y() + yInc * step;
            Piece piece = game.getPiece(checkX, checkY);
            if (piece != null) {
                piecesInBetween++;
            }
        }

        Piece destPiece = game.getPiece(destination);
        if (destPiece != null) {
            return piecesInBetween == 1;
        } else {
            return piecesInBetween == 0;
        }
    }
}
