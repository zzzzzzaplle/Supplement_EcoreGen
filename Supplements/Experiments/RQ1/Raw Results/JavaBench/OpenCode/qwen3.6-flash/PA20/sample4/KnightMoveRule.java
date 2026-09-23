public class KnightMoveRule implements Rule {

    public KnightMoveRule() {
    }

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());

        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
