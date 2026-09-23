public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        if (!((dx == 0) || (dy == 0))) {
            return false;
        }
        return true;
    }
    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
