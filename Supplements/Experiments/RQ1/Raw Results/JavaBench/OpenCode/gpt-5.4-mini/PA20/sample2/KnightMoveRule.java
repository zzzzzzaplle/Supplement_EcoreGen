public class KnightMoveRule implements Rule {
    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().getX() - move.getSource().getX());
        int dy = Math.abs(move.getDestination().getY() - move.getSource().getY());
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    public String getDescription() {
        return "knight move rule is violated";
    }
}
