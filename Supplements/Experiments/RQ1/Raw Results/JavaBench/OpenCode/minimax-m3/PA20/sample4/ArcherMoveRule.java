public class ArcherMoveRule implements Rule {

    public ArcherMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int dx = move.getDestination().x();
        int dy = move.getDestination().y();
        int stepX = Integer.compare(dx, sx);
        int stepY = Integer.compare(dy, sy);
        if (stepX != 0 && stepY != 0) {
            return false;
        }
        if (stepX == 0 && stepY == 0) {
            return false;
        }
        int betweenPieces = 0;
        int cx = sx + stepX;
        int cy = sy + stepY;
        while (cx != dx || cy != dy) {
            if (game.getPiece(new Place(cx, cy)) != null) {
                betweenPieces++;
            }
            cx += stepX;
            cy += stepY;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece == null) {
            return betweenPieces == 0;
        } else {
            return betweenPieces == 1;
        }
    }
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
