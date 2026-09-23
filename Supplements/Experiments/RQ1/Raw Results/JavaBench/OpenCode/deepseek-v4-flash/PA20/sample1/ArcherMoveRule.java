public class ArcherMoveRule implements Rule {

    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        if (dx != 0 && dy != 0) {
            return false;
        }
        int stepX = dx == 0 ? 0 : (dx > 0 ? 1 : -1);
        int stepY = dy == 0 ? 0 : (dy > 0 ? 1 : -1);
        int count = 0;
        int x = move.getSource().x() + stepX;
        int y = move.getSource().y() + stepY;
        while (x != move.getDestination().x() || y != move.getDestination().y()) {
            if (game.getPiece(x, y) != null) {
                count++;
            }
            x += stepX;
            y += stepY;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            return count == 0;
        } else {
            return count == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
