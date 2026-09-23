public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();

        if (!((Math.abs(dx) == 0) ^ (Math.abs(dy) == 0))) {
            return false;
        }

        boolean isCapture = game.getPiece(destination) != null;
        if (!isCapture) {
            for (int step = 1; step < Math.max(Math.abs(dx), Math.abs(dy)); step++) {
                int checkX, checkY;
                if (dx != 0) {
                    checkX = source.x() + (step * dx / Math.abs(dx));
                    checkY = source.y();
                } else {
                    checkX = source.x();
                    checkY = source.y() + (step * dy / Math.abs(dy));
                }
                if (game.getPiece(checkX, checkY) != null) {
                    return false;
                }
            }
        } else {
            int count = 0;
            for (int step = 1; step < Math.max(Math.abs(dx), Math.abs(dy)); step++) {
                int checkX, checkY;
                if (dx != 0) {
                    checkX = source.x() + (step * dx / Math.abs(dx));
                    checkY = source.y();
                } else {
                    checkX = source.x();
                    checkY = source.y() + (step * dy / Math.abs(dy));
                }
                if (game.getPiece(checkX, checkY) != null) {
                    count++;
                }
            }
            return count == 1;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
