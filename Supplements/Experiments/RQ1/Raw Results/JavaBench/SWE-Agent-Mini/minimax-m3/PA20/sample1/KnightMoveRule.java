public class KnightMoveRule implements Rule {

    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true;
        }
        int sx = move.getSource().x();
        int sy = move.getSource().y();
        int tx = move.getDestination().x();
        int ty = move.getDestination().y();
        int dx = tx - sx;
        int dy = ty - sy;
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        return (absDx == 2 && absDy == 1) || (absDx == 1 && absDy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
